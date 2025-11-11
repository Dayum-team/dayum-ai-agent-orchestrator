package dayum.aiagent.orchestrator.application.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.chat.ChatService;
import dayum.aiagent.orchestrator.application.conversation.ConversationService;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.client.s3.S3ClientService;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AiChatHandler extends TextWebSocketHandler {

  private final Map<String, UploadSession> uploads = new ConcurrentHashMap<>();

  private static final long MAX_UPLOAD_SIZE = 50L * 1024 * 1024; // 50MB
  private static final int  RECOMMENDED_CHUNK_B64 = 256 * 1024;  // 안내용
  private static final java.time.Duration UPLOAD_IDLE_TIMEOUT = java.time.Duration.ofMinutes(10);
  private final ChatService chatService;
  private final ObjectMapper om;
  private final S3ClientService s3ClientService;

  private final ConversationService conversationService;

  private final Map<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
  static final class UploadSession {
    final long memberId;
    final String filename;
    final String mimeType;
    final long size;
    final int totalChunks;
    final String sha256;
    final byte[][] chunks;
    long receivedBytes = 0;
    long lastTouchMs = System.currentTimeMillis();
    UploadSession(long memberId, String filename, String mimeType, long size, int totalChunks, String sha256) {
      this.memberId = memberId;
      this.filename = filename;
      this.mimeType = mimeType;
      this.size = size;
      this.totalChunks = totalChunks;
      this.sha256 = sha256;
      this.chunks = new byte[totalChunks][];
    }
    void touch() { lastTouchMs = System.currentTimeMillis(); }
  }
  @Override
  public void afterConnectionEstablished(WebSocketSession s) {
    Long mid = (Long) s.getAttributes().get("memberId");
    if (mid == null) { safeClose(s, CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized")); return; }
    rooms.computeIfAbsent(mid, k -> ConcurrentHashMap.newKeySet()).add(s);
    send(s, Map.of("event", "connected", "memberId", mid, "roomId", mid));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession s, CloseStatus status) {
    Long mid = (Long) s.getAttributes().get("memberId");
    if (mid == null) return;
    var set = rooms.getOrDefault(mid, Collections.emptySet());
    set.remove(s);
    if (set.isEmpty()) rooms.remove(mid);
  }

  @Override
  protected void handleTextMessage(WebSocketSession s, TextMessage msg) throws Exception {
    Long midObj = (Long) s.getAttributes().get("memberId");
    if (midObj == null) { sendErr(s, "UNAUTHORIZED", "memberId missing"); safeClose(s, CloseStatus.NOT_ACCEPTABLE); return; }
    long memberId = midObj;

    Map<String, Object> in = om.readValue(msg.getPayload(), Map.class);
    String event = str(in.get("event"));
    if (event == null || event.isBlank()) { sendErr(s, "BAD_FORMAT", "event 필수"); return; }

    switch (event) {
      case "connection" -> send(s, Map.of("event","connected","memberId",memberId,"roomId",memberId));
      case "disconnect" -> { send(s, Map.of("event","disconnected","reason","client request")); safeClose(s, CloseStatus.NORMAL); }
      case "joinRoom" -> send(s, Map.of("event","joined","roomId",memberId));
      case "leaveRoom" -> send(s, Map.of("event","left","roomId",memberId));

      case "sendMessage" -> handleSendTextMessage(s, memberId, in);

      case "sendFileStart" -> handleFileStart(s, memberId, in);
      case "sendFileChunk" -> handleFileChunk(s, memberId, in);
      case "sendFileEnd"   -> handleFileEnd(s, memberId, in);
      default -> sendErr(s, "UNKNOWN_EVENT", "지원하지 않는 event: " + event);
    }
  }
  private void handleFileStart(WebSocketSession s, long memberId, Map<String, Object> in) {
    String uploadId = str(in.get("uploadId"));
    String filename  = str(in.get("filename"));
    String mimeType  = str(in.get("mimeType"));
    Number sizeN     = (Number) in.get("size");
    Number totalN    = (Number) in.get("totalChunks");
    String sha256    = str(in.get("sha256"));

    if (uploadId == null || filename == null || mimeType == null || sizeN == null || totalN == null) {
      sendErr(s, "BAD_FORMAT", "uploadId/filename/mimeType/size/totalChunks 필수"); return;
    }
    long size = sizeN.longValue();
    int total = totalN.intValue();
    if (size <= 0 || size > MAX_UPLOAD_SIZE) { sendErr(s, "FILE_TOO_LARGE", "최대 50MB"); return; }
    if (total <= 0) { sendErr(s, "BAD_FORMAT", "totalChunks > 0 필요"); return; }

    uploads.put(uploadId, new UploadSession(memberId, filename, mimeType, size, total, sha256));
    send(s, Map.of("event","fileStartAck","uploadId", uploadId, "recommendedChunkBase64", RECOMMENDED_CHUNK_B64));
  }
  private void handleFileChunk(WebSocketSession s, long memberId, Map<String, Object> in) {
    String uploadId = str(in.get("uploadId"));
    Number idxN     = (Number) in.get("chunkIndex");
    String dataB64  = str(in.get("dataBase64"));

    var us = uploads.get(uploadId);
    if (us == null) { sendErr(s, "UPLOAD_NOT_FOUND", "sendFileStart 먼저 호출 필요"); return; }
    if (us.memberId != memberId) { sendErr(s, "FORBIDDEN", "본인 업로드만 가능"); return; }
    if (idxN == null || dataB64 == null) { sendErr(s, "BAD_FORMAT", "chunkIndex/dataBase64 필수"); return; }

    int idx = idxN.intValue();
    if (idx < 0 || idx >= us.totalChunks) { sendErr(s, "BAD_INDEX", "범위 밖 청크"); return; }

    byte[] bytes;
    try {
      bytes = java.util.Base64.getDecoder().decode(dataB64.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    } catch (IllegalArgumentException e) {
      sendErr(s, "BAD_BASE64", "디코딩 실패"); return;
    }

    if (us.chunks[idx] == null) {
      us.chunks[idx] = bytes;
      us.receivedBytes += bytes.length;
    }
    us.touch();

    send(s, Map.of("event","fileChunkAck","uploadId", uploadId, "chunkIndex", idx));
  }
  private void handleFileEnd(WebSocketSession s, long memberId, Map<String, Object> in) {
    String uploadId = str(in.get("uploadId"));
    var us = uploads.get(uploadId);
    if (us == null) { sendErr(s, "UPLOAD_NOT_FOUND", "세션 없음"); return; }
    if (us.memberId != memberId) { sendErr(s, "FORBIDDEN", "본인 업로드만 가능"); return; }

    for (int i = 0; i < us.totalChunks; i++) {
      if (us.chunks[i] == null) { sendErr(s, "CHUNK_MISSING", "누락 청크 index=" + i); return; }
    }
    if (us.receivedBytes <= 0 || us.receivedBytes > MAX_UPLOAD_SIZE) {
      sendErr(s, "SIZE_MISMATCH", "수신 크기 이상"); uploads.remove(uploadId); return;
    }

    byte[] all = new byte[(int) us.receivedBytes];
    int pos = 0;
    for (int i = 0; i < us.totalChunks; i++) {
      System.arraycopy(us.chunks[i], 0, all, pos, us.chunks[i].length);
      pos += us.chunks[i].length;
    }

    if (us.sha256 != null) {
      String calc = sha256Hex(all);
      if (!calc.equalsIgnoreCase(us.sha256)) {
        sendErr(s, "HASH_MISMATCH", "무결성 실패"); uploads.remove(uploadId); return;
      }
    }

    String datePrefix = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).toString().replace("-", "/");
    String prefix = "chat/" + memberId + "/" + datePrefix;

    String url;
    try {
      url = s3ClientService.uploadBytes(prefix, us.filename, all);
    } catch (Exception e) {
      sendErr(s, "STORE_FAILED", "S3 업로드 실패: " + e.getMessage());
      uploads.remove(uploadId);
      return;
    }

    var savedUser = chatService.saveUser(memberId, "[file] " + url, null);
    Map<String,Object> userOut = Map.of(
        "event","receiveMessage",
        "message", Map.of(
            "messageId", savedUser.getChatId(),
            "roomId", memberId,
            "role", "USER",
            "type", us.mimeType.startsWith("video/") ? "video" : "image",
            "mediaUrl", url,
            "meta", Map.of("mimeType", us.mimeType, "size", us.size),
            "createdAt", savedUser.getCreatedAt().toString()
        )
    );
    broadcast(memberId, userOut);

    String sessionId = getSessionId(s);
    UserMessage mediaMsg = new UserMessage(null, null, url);
    List<PlaybookResult> results = conversationService.chat(memberId, sessionId, mediaMsg);

    if (results != null) {
      for (PlaybookResult r : results) {
        String resp = extractText(r);
        if (resp != null && !resp.isBlank()) {
          var savedAi = chatService.saveSystem(memberId, resp);
          Map<String,Object> aiOut = Map.of(
              "event","receiveMessage",
              "message", Map.of(
                  "messageId", savedAi.getChatId(),
                  "roomId", memberId,
                  "role", "SYSTEM",
                  "type", "text",
                  "content", resp,
                  "createdAt", savedAi.getCreatedAt().toString()
              )
          );
          broadcast(memberId, aiOut);
        } else {
          broadcastAiResultGeneric(memberId, r);
        }
      }
    }

    uploads.remove(uploadId);
    send(s, Map.of("event","fileEndAck","uploadId", uploadId));
  }
  private static String sha256Hex(byte[] data) {
    try {
      var md = java.security.MessageDigest.getInstance("SHA-256");
      byte[] d = md.digest(data);
      StringBuilder sb = new StringBuilder();
      for (byte b : d) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) { throw new RuntimeException(e); }
  }

  private void reapIdleUploads() {
    long now = System.currentTimeMillis();
    long idleMs = UPLOAD_IDLE_TIMEOUT.toMillis();
    uploads.entrySet().removeIf(e -> now - e.getValue().lastTouchMs > idleMs);
  }
  private void handleSendTextMessage(WebSocketSession s, long memberId, Map<String, Object> in) {
    String content  = str(in.get("content"));
    String clientId = str(in.get("clientMessageId"));
    if (content == null || content.isBlank()) { sendErr(s, "BAD_FORMAT", "content 필수"); return; }

    var savedUser = chatService.saveUser(memberId, content, clientId);
    send(s, Map.of("event","messageAck","clientMessageId",clientId,"messageId",savedUser.getChatId()));

    String sessionId = getSessionId(s);
    var user = new dayum.aiagent.orchestrator.common.vo.UserMessage(content, null, null);
    List<PlaybookResult> results = conversationService.chat(memberId, sessionId, user);

    String text = null;
    if (results != null) {
      for (PlaybookResult r : results) {
        text = extractText(r);
        if (text != null && !text.isBlank()) break;
      }
    }
    if (text == null || text.isBlank()) {
      text = "응답을 생성했어요. (디버그: 결과에 텍스트가 없었습니다)";
    }

    var savedAi = chatService.saveSystem(memberId, text);
    Map<String,Object> out = Map.of(
        "event","receiveMessage",
        "message", Map.of(
            "messageId", savedAi.getChatId(),
            "roomId", memberId,
            "role", "SYSTEM",
            "type", "text",
            "content", text,
            "createdAt", savedAi.getCreatedAt().toString()
        )
    );
    broadcast(memberId, out);
  }

  private String getSessionId(WebSocketSession s) {
    Object v = s.getAttributes().get("sessionId");
    return v == null ? String.valueOf(s.getAttributes().get("memberId")) : String.valueOf(v);
  }

  private void broadcast(long memberId, Map<String, Object> payload) {
    var set = rooms.get(memberId); if (set == null) return;
    TextMessage tm = toJson(payload);
    for (WebSocketSession x : set) if (x.isOpen()) try { x.sendMessage(tm); } catch (Exception ignored) {}
  }

  private void broadcastAiResultGeneric(long memberId, PlaybookResult r) {
    String type = "text";
    String content = extractText(r);
    String mediaUrl = extractMediaUrl(r);
    if (mediaUrl != null) {
      type = guessMediaType(r);
    }
    Map<String,Object> msg = new HashMap<>();
    msg.put("messageId", -1);
    msg.put("roomId", memberId);
    msg.put("role", "SYSTEM");
    msg.put("type", type);
    if (content != null) msg.put("content", content);
    if (mediaUrl != null) msg.put("mediaUrl", mediaUrl);
    msg.put("createdAt", java.time.LocalDateTime.now().toString());

    broadcast(memberId, Map.of("event","receiveMessage","message", msg));
  }

  private String extractText(PlaybookResult r) {
    try { var m = r.getClass().getMethod("getText");     Object v = m.invoke(r); if (v instanceof String s && !s.isBlank()) return s; } catch (Exception ignored) {}
    try { var m = r.getClass().getMethod("getContent");  Object v = m.invoke(r); if (v instanceof String s && !s.isBlank()) return s; } catch (Exception ignored) {}
    try { var m = r.getClass().getMethod("getReply");    Object v = m.invoke(r); if (v != null) {
      var mm = v.getClass().getMethod("getText"); Object vv = mm.invoke(v);
      if (vv instanceof String s && !s.isBlank()) return s; } } catch (Exception ignored) {}
    try { var m = r.getClass().getMethod("toString");    Object v = m.invoke(r); if (v instanceof String s && !s.isBlank()) return s; } catch (Exception ignored) {}
    return null;
  }

  private String extractMediaUrl(PlaybookResult r) {
    try {
      var m = r.getClass().getMethod("getMediaUrl");
      Object v = m.invoke(r);
      if (v instanceof String s && !s.isBlank()) return s;
    } catch (Exception ignored) {}
    return null;
  }

  private String guessMediaType(PlaybookResult r) {
    try {
      var m = r.getClass().getMethod("getType");
      Object v = m.invoke(r);
      if (v instanceof String s && !s.isBlank()) return s;
    } catch (Exception ignored) {}
    return "image";
  }

  private void send(WebSocketSession s, Map<String, Object> payload) {
    try { s.sendMessage(toJson(payload)); } catch (Exception ignored) {}
  }
  private void sendErr(WebSocketSession s, String code, String msg) {
    send(s, Map.of("event","error","code",code,"message",msg));
  }
  private TextMessage toJson(Object o) {
    try { return new TextMessage(om.writeValueAsString(o)); }
    catch (Exception e) { throw new RuntimeException(e); }
  }
  private void safeClose(WebSocketSession s, CloseStatus st) { try { s.close(st); } catch (Exception ignored) {} }
  private String str(Object v) { return v == null ? null : String.valueOf(v); }
}

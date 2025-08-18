package dayum.aiagent.orchestrator.application.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 프론트 이벤트 스펙
 * - 연결/해제: connection, disconnect  (서버 → connected / disconnected)
 * - 방 입장/퇴장: joinRoom, leaveRoom  (서버 → joined / left)
 * - 메시지: sendMessage              (서버 → messageAck / receiveMessage)
 *
 * 방은 1인 1방이므로 roomId = memberId 로 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class AiChatHandler extends TextWebSocketHandler {

  private final ChatService chatService;
  private final ObjectMapper om;

  private final Map<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    Long memberIdObj = (Long) session.getAttributes().get("memberId");
    if (memberIdObj == null) {
      safeClose(session, CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
      return;
    }

    long memberId = memberIdObj;
    rooms.computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet()).add(session);

    send(session, Map.of("event", "connected", "memberId", memberId, "roomId", memberId));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage text) throws Exception {
    Long memberIdObj = (Long) session.getAttributes().get("memberId");
    if (memberIdObj == null) {
      sendErr(session, "UNAUTHORIZED", "memberId missing in session");
      safeClose(session, CloseStatus.NOT_ACCEPTABLE);
      return;
    }
    long memberId = memberIdObj;

    Map<String, Object> in = om.readValue(text.getPayload(), Map.class);
    String event = (String) in.get("event");
    if (event == null || event.isBlank()) {
      sendErr(session, "BAD_FORMAT", "event 필드가 필요합니다");
      return;
    }

    switch (event) {
      case "connection" -> {
        send(session, Map.of("event", "connected", "memberId", memberId, "roomId", memberId));
      }

      case "disconnect" -> {
        send(session, Map.of("event", "disconnected", "reason", "client request"));
        safeClose(session, CloseStatus.NORMAL);
      }

      case "joinRoom" -> {
        send(session, Map.of("event", "joined", "roomId", memberId));
      }

      case "leaveRoom" -> {
        send(session, Map.of("event", "left", "roomId", memberId));
      }

      case "sendMessage" -> {
        String content = str(in.get("content"));
        String clientId = str(in.get("clientMessageId"));
        if (content == null || content.isBlank()) {
          sendErr(session, "BAD_FORMAT", "content 필수");
          return;
        }

        var savedUser = chatService.saveUser(memberId, content, clientId);

        // 2) ACK
        send(session, Map.of(
            "event", "messageAck",
            "clientMessageId", clientId,
            "messageId", savedUser.getChatId()
        ));

        String reply = "AI: " + content;
        var savedAi = chatService.saveSystem(memberId, reply);

        Map<String, Object> out = Map.of(
            "event", "receiveMessage",
            "message", Map.of(
                "messageId", savedAi.getChatId(),
                "roomId", memberId,
                "role", "SYSTEM",
                "content", reply,
                "createdAt", savedAi.getCreatedAt().toString()
            )
        );
        broadcast(memberId, out);
      }

      default -> sendErr(session, "UNKNOWN_EVENT", "지원하지 않는 event: " + event);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    Long memberIdObj = (Long) session.getAttributes().get("memberId");
    if (memberIdObj == null) return;

    long memberId = memberIdObj;
    var set = rooms.getOrDefault(memberId, Collections.emptySet());
    set.remove(session);
    if (set.isEmpty()) rooms.remove(memberId);
  }

  // ---------- helpers ----------
  private void broadcast(long memberId, Map<String, Object> payload) {
    var set = rooms.get(memberId);
    if (set == null) return;

    TextMessage tm = toJson(payload);
    for (WebSocketSession s : set) {
      if (!s.isOpen()) continue;
      try { s.sendMessage(tm); } catch (Exception ignored) {}
    }
  }

  private void send(WebSocketSession s, Map<String, Object> payload) {
    try { s.sendMessage(toJson(payload)); } catch (Exception ignored) {}
  }

  private void sendErr(WebSocketSession s, String code, String msg) {
    send(s, Map.of("event", "error", "code", code, "message", msg));
  }

  private TextMessage toJson(Object o) {
    try { return new TextMessage(om.writeValueAsString(o)); }
    catch (Exception e) { throw new RuntimeException(e); }
  }

  private void safeClose(WebSocketSession s, CloseStatus status) {
    try { s.close(status); } catch (Exception ignored) {}
  }

  private String str(Object v) {
    return (v == null) ? null : String.valueOf(v);
  }
}

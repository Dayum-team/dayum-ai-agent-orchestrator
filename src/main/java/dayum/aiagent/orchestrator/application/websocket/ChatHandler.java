package dayum.aiagent.orchestrator.application.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatHandler extends TextWebSocketHandler {
  private final ObjectMapper om = new ObjectMapper();

  private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
  private final Map<String, String> sessionRoom = new ConcurrentHashMap<>();
  private final Map<String, String> sessionSender = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    URI uri = session.getUri();
    Map<String, String> q = parse(uri != null ? uri.getQuery() : null);
    String roomId = q.getOrDefault("roomId", "default");
    String sender = q.getOrDefault("sender", "anon");

    rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    sessionRoom.put(session.getId(), roomId);
    sessionSender.put(session.getId(), sender);

    broadcast(roomId, new Msg("ENTER", roomId, "SYSTEM", sender + " 님 입장"));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String roomId = sessionRoom.get(session.getId());
    String sender = sessionSender.get(session.getId());
    if (roomId == null) return;

    Msg in = om.readValue(message.getPayload(), Msg.class);
    broadcast(roomId, new Msg("TALK", roomId, sender, in.getContent()));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String roomId = sessionRoom.remove(session.getId());
    String sender = sessionSender.remove(session.getId());
    if (roomId != null) {
      Set<WebSocketSession> set = rooms.getOrDefault(roomId, Collections.emptySet());
      set.remove(session);
      broadcast(roomId, new Msg("LEAVE", roomId, "SYSTEM", (sender!=null?sender:"누군가") + " 님 퇴장"));
      if (set.isEmpty()) rooms.remove(roomId);
    }
  }

  private void broadcast(String roomId, Msg msg) {
    Set<WebSocketSession> set = rooms.get(roomId);
    if (set == null) return;
    try {
      String payload = om.writeValueAsString(msg);
      TextMessage tm = new TextMessage(payload);
      for (WebSocketSession s : set) if (s.isOpen()) s.sendMessage(tm);
    } catch (Exception ignored) {}
  }

  private Map<String, String> parse(String query) {
    Map<String, String> m = new HashMap<>();
    if (query == null || query.isEmpty()) return m;
    for (String p : query.split("&")) {
      String[] kv = p.split("=", 2);
      if (kv.length == 2) m.put(kv[0], java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8));
    }
    return m;
  }

  public static class Msg {
    private String type;   // ENTER/TALK/LEAVE
    private String roomId;
    private String sender;
    private String content;
    public Msg() {}
    public Msg(String type, String roomId, String sender, String content) {
      this.type = type; this.roomId = roomId; this.sender = sender; this.content = content;
    }
    // getters/setters
    public String getType(){return type;} public void setType(String t){this.type=t;}
    public String getRoomId(){return roomId;} public void setRoomId(String r){this.roomId=r;}
    public String getSender(){return sender;} public void setSender(String s){this.sender=s;}
    public String getContent(){return content;} public void setContent(String c){this.content=c;}
  }
}

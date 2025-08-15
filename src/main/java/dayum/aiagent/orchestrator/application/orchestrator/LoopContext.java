package dayum.aiagent.orchestrator.application.orchestrator;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LoopContext {
  // Loop 내의 요청/응답 메시지
  private List<Message> messages = new ArrayList<>();

  // 최대 반복 횟수
  private static final int MAX_ITERATIONS = 10;

  public void addRequest(String content) {
    messages.add(new Message(MessageType.REQUEST, content));
  }

  public void addResponse(String content) {
    messages.add(new Message(MessageType.RESPONSE, content));
  }

  public void addToolResult(String toolName, String arguments, String result) {
    String content =
        String.format("Tool: %s\nArguments: %s\nResult: %s", toolName, arguments, result);
    messages.add(new Message(MessageType.TOOL_RESULT, content));
  }

  public boolean canContinue() {
    long requestCount = messages.stream().filter(m -> m.getType() == MessageType.REQUEST).count();
    return requestCount < MAX_ITERATIONS;
  }

  // 대화 이력 요약
  public String getSummary() {
    return "";
  }

  public enum MessageType {
    REQUEST,
    RESPONSE,
    TOOL_RESULT
  }

  @Data
  public static class Message {
    private final MessageType type;
    private final String content;
  }
}

package dayum.aiagent.orchestrator.client.chat.dto;

import java.util.List;

public record ChatCompletionResponse(
    String role, String message, List<SelectedFunction> functions, String finishReason) {

  public record SelectedFunction(String id, String name, String arguments) {}
}

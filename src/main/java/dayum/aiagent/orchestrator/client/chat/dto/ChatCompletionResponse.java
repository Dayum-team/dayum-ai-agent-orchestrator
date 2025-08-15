package dayum.aiagent.orchestrator.client.chat.dto;

import jakarta.annotation.Nullable;
import java.util.List;

public record ChatCompletionResponse(
    String role, String message, @Nullable List<ToolCalls> toolCalls) {

  public record ToolCalls(String name, String partialJson) {}
}

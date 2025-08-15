package dayum.aiagent.orchestrator.client.chat.dto;

import jakarta.annotation.Nullable;
import java.util.List;

public record ChatCompletionResponse(
    String role, String message, @Nullable List<SelectedFunction> functions) {

  public record SelectedFunction(String id, String name, String arguments) {}
}

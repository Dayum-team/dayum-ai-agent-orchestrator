package dayum.aiagent.orchestrator.client.chat.clova.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.List;

public record ClovaChatCompletionResponse(Status status, Result result) {
  public record Status(String code, String message) {}

  public record Result(
      Message message,
      String finishReason,
      Integer inputLength,
      Integer outputLength,
      List<AiFilter> aiFilter) {}

  public record Message(
      String role, String content, @JsonSetter(nulls = Nulls.AS_EMPTY) List<ToolCalls> toolCalls) {}

  public record AiFilter(String groupName, String name, Double score, String result) {}

  public record ToolCalls(String id, String type, Function function) {}

  public record Function(String name, String partialJson) {}
}

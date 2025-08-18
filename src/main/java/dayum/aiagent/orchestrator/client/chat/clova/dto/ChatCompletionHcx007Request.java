package dayum.aiagent.orchestrator.client.chat.clova.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dayum.aiagent.orchestrator.client.chat.clova.ClovaStudioProperties;
import dayum.aiagent.orchestrator.client.chat.schema.SchemaFactory;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionHcx007Request(
    List<Message> messages,
    List<SchemaFactory.Function> tools,
    SchemaFactory.Json responseFormat,
    Thinking thinking,
    String toolChoice,
    Double topP,
    Integer topK,
    Integer maxCompletionTokens,
    Double temperature,
    Double repetitionPenalty,
    List<String> stop,
    long seed,
    Boolean includeAiFilters)
    implements ChatCompletionRequest {

  public static ChatCompletionHcx007Request of(String systemMessage, String userMessage) {
    return ChatCompletionHcx007Request.builder()
        .messages(
            List.of(
                new Message("system", List.of(new TextContent("text", systemMessage))),
                new Message("user", List.of(new TextContent("text", userMessage)))))
        .thinking(new Thinking("none"))
        .maxCompletionTokens(ClovaStudioProperties.ModelConfig.MAX_TOKENS)
        .topP(1.0)
        .temperature(0.0)
        .stop(List.of())
        .build();
  }

  public static ChatCompletionHcx007Request of(
      String systemMessage, String userMessage, SchemaFactory.JsonSchema responseFormat) {
    return ChatCompletionHcx007Request.builder()
        .messages(
            List.of(
                new Message("system", List.of(new TextContent("text", systemMessage))),
                new Message("user", List.of(new TextContent("text", userMessage)))))
        .responseFormat(new SchemaFactory.Json("json", responseFormat))
        .thinking(new Thinking("none"))
        .maxCompletionTokens(ClovaStudioProperties.ModelConfig.MAX_TOKENS)
        .topP(1.0)
        .temperature(0.0)
        .stop(List.of())
        .build();
  }
}

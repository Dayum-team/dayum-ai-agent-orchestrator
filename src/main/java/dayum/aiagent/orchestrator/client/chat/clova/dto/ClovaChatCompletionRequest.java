package dayum.aiagent.orchestrator.client.chat.clova.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dayum.aiagent.orchestrator.client.chat.clova.ClovaStudioProperties;
import dayum.aiagent.orchestrator.client.chat.dto.Schema;
import java.util.List;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClovaChatCompletionRequest(
    List<Message> messages,
    List<Schema.Function> tools,
    Schema.Json responseFormat,
    Thinking thinking,
    String toolChoice,
    Double topP,
    Integer topK,
    Integer maxTokens,
    Double temperature,
    Double repetitionPenalty,
    List<String> stop,
    long seed,
    Boolean includeAiFilters) {

  public record Message(String role, List<Content> content) {}

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = TextContent.class, name = "text"),
    @JsonSubTypes.Type(value = ImageContent.class, name = "image_url")
  })
  public interface Content {}

  public record TextContent(String type, String text) implements Content {}

  public record ImageContent(String type, @JsonProperty("imageUrl") ImageUrl imageUrl)
      implements Content {}

  public record ImageUrl(String url) {}

  public record Thinking(String effort) {}

  public static ClovaChatCompletionRequest of(String systemMessage, String userMessage) {
    return ClovaChatCompletionRequest.builder()
        .messages(
            List.of(
                new Message("system", List.of(new TextContent("text", systemMessage))),
                new Message("user", List.of(new TextContent("text", userMessage)))))
        .toolChoice("none")
        .topP(ClovaStudioProperties.ModelConfig.TOP_P)
        .topK(ClovaStudioProperties.ModelConfig.TOP_K)
        .maxTokens(ClovaStudioProperties.ModelConfig.MAX_TOKENS)
        .temperature(ClovaStudioProperties.ModelConfig.TEMPERATURE)
        .repetitionPenalty(ClovaStudioProperties.ModelConfig.REPETITION_PENALTY)
        .stop(List.of())
        .seed(ClovaStudioProperties.ModelConfig.SEED)
        .includeAiFilters(ClovaStudioProperties.ModelConfig.INCLUDE_AI_FILTERS)
        .build();
  }

  public static ClovaChatCompletionRequest forFunctionCalling(
      String systemMessage, String userMessage, List<Schema.ToolSchema> tools) {
    return ClovaChatCompletionRequest.builder()
        .messages(
            List.of(
                new Message("system", List.of(new TextContent("text", systemMessage))),
                new Message("user", List.of(new TextContent("text", userMessage)))))
        .tools(tools.stream().map(tool -> new Schema.Function("function", tool)).toList())
        .toolChoice("auto")
        // .topP(ClovaStudioProperties.ModelConfig.TOP_P)
        // .topK(ClovaStudioProperties.ModelConfig.TOP_K)
        // .maxTokens(ClovaStudioProperties.ModelConfig.MAX_TOKENS)
        // .temperature(ClovaStudioProperties.ModelConfig.TEMPERATURE)
        // .repetitionPenalty(ClovaStudioProperties.ModelConfig.REPETITION_PENALTY)
        // .stop(List.of())
        // .seed(ClovaStudioProperties.ModelConfig.SEED)
        // .includeAiFilters(ClovaStudioProperties.ModelConfig.INCLUDE_AI_FILTERS)
        .build();
  }

  public static ClovaChatCompletionRequest forStructuredOutput(
      String systemMessage, String userMessage, Schema.JsonSchema responseFormat) {
    return ClovaChatCompletionRequest.builder()
        .messages(
            List.of(
                new Message("system", List.of(new TextContent("text", systemMessage))),
                new Message("user", List.of(new TextContent("text", userMessage)))))
        .responseFormat(new Schema.Json("json", responseFormat))
        .thinking(new Thinking("none"))
        .topP(1.0)
        .temperature(0.0)
        .repetitionPenalty(ClovaStudioProperties.ModelConfig.REPETITION_PENALTY)
        .stop(List.of())
        .build();
  }
}

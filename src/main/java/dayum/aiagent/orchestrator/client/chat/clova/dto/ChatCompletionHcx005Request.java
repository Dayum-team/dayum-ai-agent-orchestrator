package dayum.aiagent.orchestrator.client.chat.clova.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dayum.aiagent.orchestrator.client.chat.clova.ClovaStudioProperties;
import dayum.aiagent.orchestrator.client.chat.schema.SchemaFactory;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionHcx005Request(
    List<ChatCompletionRequest.Message> messages,
    List<SchemaFactory.Function> tools,
    String toolChoice,
    Double topP,
    Integer topK,
    Integer maxTokens,
    Double temperature,
    Double repetitionPenalty,
    List<String> stop,
    long seed,
    Boolean includeAiFilters)
    implements ChatCompletionRequest {

  public static ChatCompletionHcx005Request of(String systemMessage, String userMessage) {
    return ChatCompletionHcx005Request.builder()
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

  public static ChatCompletionHcx005Request ofImage(
      String systemMessage, String userMessage, String imageUrl) {
    return ChatCompletionHcx005Request.builder()
        .messages(
            List.of(
                new Message("system", List.of(new TextContent("text", systemMessage))),
                new Message(
                    "user",
                    List.of(
                        new ImageContent("image_url", new ImageUrl(imageUrl)),
                        new TextContent("text", userMessage)))))
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
}

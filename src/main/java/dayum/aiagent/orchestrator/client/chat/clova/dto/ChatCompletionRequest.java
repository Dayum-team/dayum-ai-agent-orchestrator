package dayum.aiagent.orchestrator.client.chat.clova.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dayum.aiagent.orchestrator.client.chat.ModelType;
import dayum.aiagent.orchestrator.client.chat.schema.SchemaFactory;
import java.util.List;

public sealed interface ChatCompletionRequest
    permits ChatCompletionHcx005Request, ChatCompletionHcx007Request {

  record Message(String role, List<Content> content) {}

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = TextContent.class, name = "text"),
    @JsonSubTypes.Type(value = ImageContent.class, name = "image_url")
  })
  interface Content {}

  record TextContent(String type, String text) implements Content {}

  record ImageContent(String type, ImageUrl imageUrl) implements Content {}

  record ImageUrl(String url) {}

  record Thinking(String effort) {}

  static ChatCompletionRequest of(ModelType type, String systemMessage, String userMessage) {
    return switch (type) {
      case HCX_005 -> ChatCompletionHcx005Request.of(systemMessage, userMessage);
      case HCX_007 -> ChatCompletionHcx007Request.of(systemMessage, userMessage);
    };
  }

  static ChatCompletionRequest forStructuredOutputs(
      ModelType type,
      String systemMessage,
      String userMessage,
      SchemaFactory.JsonSchema responseSchema) {
    return switch (type) {
      case HCX_005 -> throw new RuntimeException();
      case HCX_007 -> ChatCompletionHcx007Request.of(systemMessage, userMessage, responseSchema);
    };
  }
}

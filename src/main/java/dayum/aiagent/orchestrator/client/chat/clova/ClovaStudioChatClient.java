package dayum.aiagent.orchestrator.client.chat.clova;

import com.github.jknack.handlebars.Handlebars;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.client.chat.ChatClient;
import dayum.aiagent.orchestrator.client.chat.ChatPrompt;
import dayum.aiagent.orchestrator.client.chat.ModelType;
import dayum.aiagent.orchestrator.client.chat.clova.dto.ChatCompletionRequest;
import dayum.aiagent.orchestrator.client.chat.clova.dto.ClovaChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.schema.SchemaFactory.*;
import io.vavr.control.Try;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaStudioChatClient implements ChatClient {

  private final Handlebars handlebars = new Handlebars();

  private final ClovaStudioProperties properties;
  private final RestClient restClient;

  public ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, ModelType modelType) {
    try {
      var request = ChatCompletionRequest.of(modelType, systemMessage, userMessage);
      var response = this.sendRequest(request, modelType);
      log.info("Chat completion request : {}", request);
      log.info("Chat completion response : {}", response);
      return this.convert(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, ConversationContext context, ModelType modelType) {
    try {
      String contextMessage =
          handlebars
              .compileInline(ChatPrompt.CONTEXT_MESSAGE)
              .apply(
                  new HashMap<>() {
                    {
                      this.put("rollingSummary", context.rollingSummary());
                      this.put("shortTermContext", context.shortTermContexts());
                      this.put("currentContextKey", context.contexts().keySet());
                    }
                  });
      var request =
          ChatCompletionRequest.of(modelType, systemMessage, contextMessage + userMessage);
      var response = this.sendRequest(request, modelType);
      log.info("Chat completion request : {}", request);
      log.info("Chat completion response : {}", response);
      return this.convert(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ChatCompletionResponse chatCompletion(
      String systemMessage,
      String userMessage,
      String context,
      List<ToolSchema> toolSchemas,
      ModelType modelType) {
    // TODO: Not Implemented
    return null;
  }

  @Override
  public ChatCompletionResponse chatCompletionWithStructuredOutput(
      String systemMessage,
      String userMessage,
      ConversationContext context,
      JsonSchema outputSchema,
      ModelType modelType) {
    try {
      String contextMessage =
          handlebars
              .compileInline(ChatPrompt.CONTEXT_MESSAGE)
              .apply(
                  new HashMap<>() {
                    {
                      this.put("rollingSummary", context.rollingSummary());
                      this.put("shortTermContext", context.shortTermContexts());
                      this.put("currentContextKey", context.contexts().keySet());
                    }
                  });
      var request =
          ChatCompletionRequest.forStructuredOutputs(
              modelType, systemMessage, contextMessage + userMessage, outputSchema);
      var response = this.sendRequest(request, modelType);
      log.info("Chat completion request : {}", request);
      log.info("Chat completion response : {}", response);
      return this.convert(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ClovaChatCompletionResponse sendRequest(
      ChatCompletionRequest request, ModelType modelType) {
    return Try.ofCallable(
            () ->
                restClient
                    .post()
                    .uri(properties.getUrl() + "/" + modelType.getName())
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
                    .body(request)
                    .retrieve()
                    .body(ClovaChatCompletionResponse.class))
        .getOrElseThrow(ex -> new RuntimeException(ex));
  }

  private ChatCompletionResponse convert(ClovaChatCompletionResponse response) {
    return new ChatCompletionResponse(
        response.result().message().role(),
        response
            .result()
            .message()
            .content()
            .replaceAll("```json", "")
            .replaceAll("```", "")
            .trim(),
        response.result().message().toolCalls().stream()
            .map(
                toolCall ->
                    new ChatCompletionResponse.SelectedFunction(
                        toolCall.id(),
                        toolCall.function().name(),
                        toolCall.function().partialJson()))
            .toList(),
        response.result().finishReason());
  }
}

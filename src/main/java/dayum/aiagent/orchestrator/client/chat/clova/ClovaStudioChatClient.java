package dayum.aiagent.orchestrator.client.chat.clova;

import com.github.jknack.handlebars.Handlebars;
import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.ChatClient;
import dayum.aiagent.orchestrator.client.chat.ChatPrompt;
import dayum.aiagent.orchestrator.client.chat.clova.dto.ClovaChatCompletionRequest;
import dayum.aiagent.orchestrator.client.chat.clova.dto.ClovaChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema.*;
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

  public ChatCompletionResponse chatCompletion(String systemMessage, String userMessage) {
    try {
      var request = ClovaChatCompletionRequest.of(systemMessage, userMessage);
      var response = this.sendRequest(request);
      log.info("Chat completion request : {}", request);
      log.info("Chat completion response : {}", response);
      return this.convert(response);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  public ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, ConversationContext context) {
    try {
      String contextMessage =
          handlebars
              .compileInline(ChatPrompt.CONTEXT_MESSAGE)
              .apply(
                  new HashMap<>() {
                    {
                      this.put("rollingSummary", context.rollingSummary());
                      this.put("shortTermContext", context.shortTermContexts());
                    }
                  });
      var request = ClovaChatCompletionRequest.of(systemMessage, contextMessage + userMessage);
      var response = this.sendRequest(request);
      log.info("Chat completion request : {}", request);
      log.info("Chat completion response : {}", response);
      return this.convert(response);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  public ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, String context, List<ToolSchema> toolSchemas) {
    // TODO: Not Implemented
    return null;
  }

  private ClovaChatCompletionResponse sendRequest(ClovaChatCompletionRequest request) {
    return Try.ofCallable(
            () ->
                restClient
                    .post()
                    .uri(properties.getBaseUrl())
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
                    .body(request)
                    .retrieve()
                    .body(ClovaChatCompletionResponse.class))
        .getOrElseThrow(() -> new RuntimeException());
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

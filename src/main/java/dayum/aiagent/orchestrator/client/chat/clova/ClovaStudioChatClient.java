package dayum.aiagent.orchestrator.client.chat.clova;

import dayum.aiagent.orchestrator.client.chat.ChatClient;
import dayum.aiagent.orchestrator.client.chat.clova.dto.ClovaChatCompletionRequest;
import dayum.aiagent.orchestrator.client.chat.clova.dto.ClovaChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema.*;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ClovaStudioChatClient implements ChatClient {

  private final ClovaStudioProperties properties;
  private final RestClient restClient;

  public ChatCompletionResponse chatCompletion(String systemMessage, String userMessage) {
    return null;
  }

  public ChatCompletionResponse chatCompletion(String systemMessage, String userMessage, String context) {
    return null;
  }

  public ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, String context, List<ToolSchema> toolSchemas) {
    return null;
  }

  private ClovaChatCompletionResponse sendRequest(ClovaChatCompletionRequest request) {
    return restClient
        .post()
        .uri(properties.getBaseUrl())
        .header("Authorization", "Bearer " + properties.getApiKey())
        .header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
        .body(request)
        .retrieve()
        .body(ClovaChatCompletionResponse.class);
  }
}

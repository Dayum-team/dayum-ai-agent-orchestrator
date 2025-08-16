package dayum.aiagent.orchestrator.client.chat;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.Schema.*;
import java.util.List;

public interface ChatClient {

  ChatCompletionResponse chatCompletion(String systemMessage, String userMessage, ModelType modelType);

  ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, ConversationContext context, ModelType modelType);

  ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, String context, List<ToolSchema> toolSchemas, ModelType modelType);

  ChatCompletionResponse chatCompletionForStructuredMessage(
      String systemMessage, String userMessage, ConversationContext context, JsonSchema outputSchema, ModelType modelType);
}

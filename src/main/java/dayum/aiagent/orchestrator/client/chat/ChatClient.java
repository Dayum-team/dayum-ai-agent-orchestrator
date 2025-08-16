package dayum.aiagent.orchestrator.client.chat;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import java.util.List;

public interface ChatClient {

  ChatCompletionResponse chatCompletion(String systemMessage, String userMessage);

  ChatCompletionResponse chatCompletion(
      String systemMessage, String userMessage, ConversationContext context);

  ChatCompletionResponse chatCompletion(
      String systemMessage,
      String userMessage,
      String context,
      List<ToolSignatureSchema.ToolSchema> toolSchemas);
}

package dayum.aiagent.orchestrator.client.chat;

import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import java.util.List;

public interface ChatClient {

  void chatCompletion(String systemMessage, String userMessage);

  void chatCompletion(String systemMessage, String userMessage, String context);

  void chatCompletion(
      String systemMessage,
      String userMessage,
      String context,
      List<ToolSignatureSchema.ToolSchema> toolSchemas);
}

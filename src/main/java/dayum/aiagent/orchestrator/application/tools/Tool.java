package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;

public interface Tool<ReqT extends ToolRequest> {

  ToolType getType();

  ToolSignatureSchema.JsonSchema getSchema();

  Class<ReqT> getRequestType();

  Object execute(ConversationContext context, ReqT request);
}

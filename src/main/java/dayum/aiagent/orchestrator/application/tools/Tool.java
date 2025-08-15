package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.model.request.ToolRequest;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;

public interface Tool<ReqT extends ToolRequest> {

  String getName();

  String getDescription();

  ToolSignatureSchema.JsonSchema getSchema();

  Class<ReqT> getRequestType();

  Object execute(ConversationContext context, ReqT request);
}

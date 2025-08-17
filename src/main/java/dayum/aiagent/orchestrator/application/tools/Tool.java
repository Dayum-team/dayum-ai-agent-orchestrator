package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.schema.SchemaFactory;

public interface Tool<ReqT extends ToolRequest> {

  ToolType getType();

  SchemaFactory.JsonSchema getSchema();

  Class<ReqT> getRequestType();

  Object execute(ConversationContext context, ReqT request);
}

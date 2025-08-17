package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;

public interface Tool<ReqT extends ToolRequest> {

  String getName();

  String getDescription();

  ToolSet getToolSet();

  Class<ReqT> getRequestType();

  Object execute(ConversationContext context, ReqT request);
}

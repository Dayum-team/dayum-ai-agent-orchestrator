package dayum.aiagent.orchestrator.application.orchestrator;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;

public interface Planner {

  Plan requestPlan(
      List<ToolSignatureSchema.ToolSchema> toolSchemas,
      String userQuery,
      ConversationContext context,
      LoopContext loopContext);
}

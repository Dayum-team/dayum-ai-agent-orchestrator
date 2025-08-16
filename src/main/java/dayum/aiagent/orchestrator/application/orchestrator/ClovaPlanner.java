package dayum.aiagent.orchestrator.application.orchestrator;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.clova.ClovaStudioChatClient;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClovaPlanner implements Planner {

  private final ChatClientService chatClientService;

  @Override
  public Plan requestPlan(
      List<ToolSignatureSchema.ToolSchema> toolSchemas,
      String userMessage,
      ConversationContext context,
      LoopContext loopContext) {

    return chatClientService.requestPlan(toolSchemas, userMessage, context, loopContext);
  }
}

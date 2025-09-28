package dayum.aiagent.orchestrator.application.orchestrator.playbook.dietrecipe;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.common.vo.UserMessage;

public class DescriptionDetailedRecipePlaybook implements Playbook {

  @Override
  public PlaybookCatalog getCatalog() {
    return null;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    return null;
  }

  @Override
  public PlaybookType getType() {
    return null;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of();
  }
}

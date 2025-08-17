package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;

public interface Playbook {

  PlaybookCatalog getCatalog();

  PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage);

  PlaybookType getType();

  List<ContextType> getRequiresContext();
}

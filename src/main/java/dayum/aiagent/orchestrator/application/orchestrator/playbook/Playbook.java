package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;

public interface Playbook {

  PlaybookCatalog getCatalog();

  PlaybookResult play(ConversationContext context, UserMessage userMessage);
}

package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;

public interface Playbook {

  PlaybookCatalog getCatalog();

  PlaybookResponse play(ConversationContext context, UserMessage userMessage);
}

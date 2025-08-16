package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResponse;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmallTalkPlaybook implements Playbook {

  @Override
  public PlaybookCatalog getCatalog() {
    return null;
  }

  @Override
  public PlaybookResponse play(ConversationContext context, UserMessage userMessage) {
    return null;
  }
}

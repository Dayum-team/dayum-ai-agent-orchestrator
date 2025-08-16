package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntentRouter {

  private final List<Playbook> playbooks;
  private final ChatClientService chatClientService;

  public List<Playbook> route(ConversationContext context, UserMessage userMessage) {
    // 1. userMessage.selectQuickRely 를 기반으로 Routing
    // 2. CLOVA Studio 를 이용한 Routing
    return null;
  }
}

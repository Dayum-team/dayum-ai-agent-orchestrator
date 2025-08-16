package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Orchestrator {

  private final IntentRouter intentRouter;

  public String runTurn(ConversationContext context, UserMessage userMessage) {
    return "";
  }
}

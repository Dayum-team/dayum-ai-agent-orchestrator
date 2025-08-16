package dayum.aiagent.orchestrator.application.orchestrator;


import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Orchestrator {

  private final Planner planner;

  public List<PlaybookResult> runTurn(ConversationContext context, UserMessage userMessage) {
    return null;
  }
}

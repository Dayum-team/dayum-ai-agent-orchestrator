package dayum.aiagent.orchestrator.application.orchestrator;


import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Orchestrator {

  private final Planner planner;

  public List<PlaybookResponse> runTurn(ConversationContext context, UserMessage userMessage) {
    return null;
  }
}

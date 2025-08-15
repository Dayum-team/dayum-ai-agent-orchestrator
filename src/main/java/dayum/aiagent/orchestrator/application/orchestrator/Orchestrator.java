package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Orchestrator {

  private final Planner planner;

  public String runTurn(ConversationContext context, String userMessage) {
    return "";
  }
}

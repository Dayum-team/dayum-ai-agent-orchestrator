package dayum.aiagent.orchestrator.application.conversation;

import dayum.aiagent.orchestrator.application.context.ContextStoreService;
import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.Orchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {

  private final Orchestrator orchestrator;
  private final ContextStoreService contextStoreService;

  public String chat(long memberId, String sessionId, String userMessage) {
    ConversationContext context = contextStoreService.fetchBeforeContext(memberId, sessionId);
    String result = orchestrator.runTurn(context, userMessage);
    contextStoreService.update(sessionId, context, userMessage, result);
    return result;
  }
}

package dayum.aiagent.orchestrator.application.conversation;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.ContextStoreService;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.Orchestrator;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {

  private final Orchestrator orchestrator;
  private final ContextStoreService contextStoreService;

  public List<PlaybookResult> chat(long memberId, String sessionId, UserMessage userMessage) {
    ConversationContext context = contextStoreService.fetchBeforeContext(memberId, sessionId);
    System.out.println("context = " + context.contexts());
    List<PlaybookResult> result = orchestrator.runTurn(context, userMessage);
    contextStoreService.update(sessionId, context, userMessage, ""); // TODO
    System.out.println("context = " + context.contexts());
    return result;
  }
}

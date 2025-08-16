package dayum.aiagent.orchestrator.application.conversation;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.ContextStoreService;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.Orchestrator;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {

  private final Orchestrator orchestrator;
  private final ContextStoreService contextStoreService;

  public List<PlaybookResponse> chat(long memberId, String sessionId, UserMessage userMessage) {
    ConversationContext context = contextStoreService.fetchBeforeContext(memberId, sessionId);
    List<PlaybookResponse> result = orchestrator.runTurn(context, userMessage);
    contextStoreService.update(sessionId, context, userMessage, ""); // TODO
    return result;
  }
}

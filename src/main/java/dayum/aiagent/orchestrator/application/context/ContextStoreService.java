package dayum.aiagent.orchestrator.application.context;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.ShortTermContext;
import dayum.aiagent.orchestrator.application.context.port.DomainContextRepository;
import dayum.aiagent.orchestrator.application.context.port.ShortTermContextRepository;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContextStoreService {

  private final DomainContextRepository domainContextRepository;
  private final ShortTermContextRepository shortTermContextRepository;
  private final RollingSummaryService rollingSummaryService;

  public ConversationContext fetchBeforeContext(long memberId, String sessionId) {
    Map<ContextType, ContextValue> contexts = domainContextRepository.fetchBy(sessionId);
    List<ShortTermContext> shortTermContexts = shortTermContextRepository.fetchBy(sessionId);
    String rollingSummary = rollingSummaryService.fetchBy(sessionId);
    return new ConversationContext(memberId, sessionId, contexts, shortTermContexts, rollingSummary);
  }

  public void update(
      String sessionId,
      ConversationContext context,
      UserMessage userMessage,
      String receivedMessage) {
    var newContext = new ShortTermContext(userMessage, receivedMessage);
    shortTermContextRepository.append(sessionId, newContext);
    rollingSummaryService.update(sessionId, context.rollingSummary(), newContext);
  }
}

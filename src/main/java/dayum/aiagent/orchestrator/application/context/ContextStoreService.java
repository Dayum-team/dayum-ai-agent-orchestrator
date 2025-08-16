package dayum.aiagent.orchestrator.application.context;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;
import dayum.aiagent.orchestrator.application.context.port.ShortTermContextRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContextStoreService {

  private final ShortTermContextRepository shortTermContextRepository;
  private final RollingSummaryService rollingSummaryService;

  public ConversationContext fetchBeforeContext(long memberId, String sessionId) {
    List<ShortTermContext> shortTermContexts = shortTermContextRepository.fetchBy(sessionId);
    String rollingSummary = rollingSummaryService.fetchBy(sessionId);
    return new ConversationContext(memberId, sessionId, shortTermContexts, rollingSummary);
  }

  public void update(
      String sessionId, ConversationContext context, String userMessage, String receivedMessage) {
    var newContext = new ShortTermContext(userMessage, receivedMessage);
    shortTermContextRepository.append(sessionId, newContext);
    rollingSummaryService.update(sessionId, context.rollingSummary(), newContext);
  }
}

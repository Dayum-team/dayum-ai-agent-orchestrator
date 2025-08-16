package dayum.aiagent.orchestrator.application.context;

import dayum.aiagent.orchestrator.application.context.model.ShortTermContext;
import dayum.aiagent.orchestrator.application.context.port.RollingSummaryRepository;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RollingSummaryService {

  private final RollingSummaryRepository rollingSummaryRepository;
  private final ChatClientService chatClientService;

  public String fetchBy(String sessionId) {
    return rollingSummaryRepository.fetchBy(sessionId);
  }

  public void update(String sessionId, String beforeRollingSummary, ShortTermContext newContext) {
    var rollingSummary =
        chatClientService.summary(
            beforeRollingSummary, newContext.userMessage(), newContext.receivedMessage());
    rollingSummaryRepository.update(sessionId, rollingSummary);
  }
}

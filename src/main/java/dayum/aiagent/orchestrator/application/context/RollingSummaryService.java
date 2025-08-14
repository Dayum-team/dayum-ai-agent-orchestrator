package dayum.aiagent.orchestrator.application.context;

import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;
import dayum.aiagent.orchestrator.application.context.port.RollingSummaryRepository;
import dayum.aiagent.orchestrator.client.chat.ClovaStudioChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RollingSummaryService {

  private final RollingSummaryRepository rollingSummaryRepository;
  private final ClovaStudioChatClient clovaStudioChatClient;

  public String fetchBy(String sessionId) {
    return rollingSummaryRepository.fetchBy(sessionId);
  }

  public void update(String sessionId, String beforeRollingSummary, ShortTermContext newContext) {
    var rollingSummary =
        clovaStudioChatClient.summary(
            beforeRollingSummary, newContext.userMessage(), newContext.response());
    rollingSummaryRepository.update(sessionId, rollingSummary);
  }
}

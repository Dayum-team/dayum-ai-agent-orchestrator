package dayum.aiagent.orchestrator.application.context;

import dayum.aiagent.orchestrator.application.context.port.RollingSummaryRepository;
import dayum.aiagent.orchestrator.client.chat.ClovaStudioChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RollingSummaryService {

  private final RollingSummaryRepository rollingSummaryRepository;
  private final ClovaStudioChatClient clovaStudioChatClient;
}

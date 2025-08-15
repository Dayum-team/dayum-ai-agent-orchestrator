package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.client.chat.ClovaStudioChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Planner {

  private final ClovaStudioChatClient clovaStudioChatClient;
}

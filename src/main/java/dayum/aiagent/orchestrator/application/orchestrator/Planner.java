package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Planner {

  private final ChatClientService chatClientService;
}

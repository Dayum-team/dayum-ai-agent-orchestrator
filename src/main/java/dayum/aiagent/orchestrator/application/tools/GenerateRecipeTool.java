package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.client.chat.ClovaStudioChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateRecipeTool implements Tool<Object, Object> {

  private final ClovaStudioChatClient clovaStudioChatClient;

  @Override
  public String getName() {
    return "";
  }
}

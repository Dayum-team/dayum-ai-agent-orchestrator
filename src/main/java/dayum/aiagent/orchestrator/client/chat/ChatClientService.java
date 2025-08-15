package dayum.aiagent.orchestrator.client.chat;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatClientService {

  private final ChatClient chatClient;

  public String summary(String beforeRollingSummary, String userMessage, String response) {
    return "";
  }
}

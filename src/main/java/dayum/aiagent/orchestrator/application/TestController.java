package dayum.aiagent.orchestrator.application;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dayum.aiagent.orchestrator.application.conversation.ConversationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

  private final ConversationService conversationService;

  @PostMapping("/chat/test")
  public String testChat(@RequestBody TestMessage message) {
    return conversationService.chat(message.getMemberId(), message.getSession(), message.getUserMessage());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TestMessage {
    private long memberId;
    private String session;
    private String userMessage;
  }
}

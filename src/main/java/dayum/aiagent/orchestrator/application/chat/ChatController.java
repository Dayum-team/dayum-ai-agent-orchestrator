package dayum.aiagent.orchestrator.application.chat;

import dayum.aiagent.orchestrator.application.chat.model.ChatMessageResponse;
import dayum.aiagent.orchestrator.domain.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

  private final ChatService chatService;

  private Long me(String hdr) {
    if (hdr == null) throw new IllegalArgumentException("X-User-Id header required (test)");
    return Long.parseLong(hdr);
  }

  @GetMapping("/history")
  public List<ChatMessageResponse> history(
      @RequestHeader(value="X-User-Id", required=false) String user,
      @RequestParam(defaultValue="50") int limit,
      @RequestParam(required=false) Long before
  ) {
    Long memberId = me(user);
    return chatService.history(memberId, before, limit).stream()
        .map(ChatMessageResponse::from)
        .toList();
  }
}

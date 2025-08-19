package dayum.aiagent.orchestrator.application.chat.model;

import dayum.aiagent.orchestrator.domain.chat.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageResponse(
    Long chatId,
    String role,
    String content,
    LocalDateTime createdAt
) {
  public static ChatMessageResponse from(ChatMessage m) {
    return new ChatMessageResponse(
        m.getChatId(),
        m.getRole().name(),
        m.getContent(),
        m.getCreatedAt()
    );
  }
}
package dayum.aiagent.orchestrator.application.chat;

import dayum.aiagent.orchestrator.domain.chat.ChatMessage;
import dayum.aiagent.orchestrator.infrastructor.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatMessageRepository repo;

  @Transactional
  public ChatMessage saveUser(Long memberId, String content, @Nullable String clientMessageId) {
    if (clientMessageId != null) {
      return repo.findByMemberIdAndClientMessageId(memberId, clientMessageId)
          .orElseGet(() -> persist(memberId, ChatMessage.Role.USER, content, clientMessageId));
    }
    return persist(memberId, ChatMessage.Role.USER, content, null);
  }

  @Transactional
  public ChatMessage saveSystem(Long memberId, String content) {
    return persist(memberId, ChatMessage.Role.SYSTEM, content, null);
  }

  @Transactional(readOnly = true)
  public List<ChatMessage> history(Long memberId, Long beforeChatId, int limit) {
    return repo.history(memberId, beforeChatId, PageRequest.of(0, limit));
  }

  private ChatMessage persist(Long memberId, ChatMessage.Role role, String content, String clientId) {
    ChatMessage m = new ChatMessage();
    m.setMemberId(memberId);
    m.setRole(role);
    m.setContent(content);
    m.setClientMessageId(clientId);
    return repo.save(m);
  }
}

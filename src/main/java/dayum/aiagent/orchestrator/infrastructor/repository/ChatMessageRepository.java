package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.domain.chat.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  @Query("""
    SELECT m FROM ChatMessage m
     WHERE m.memberId = :memberId
       AND (:beforeId IS NULL OR m.chatId < :beforeId)
     ORDER BY m.chatId DESC
  """)
  List<ChatMessage> history(@Param("memberId") Long memberId,
      @Param("beforeId") Long beforeId,
      Pageable pageable);

  Optional<ChatMessage> findByMemberIdAndClientMessageId(Long memberId, String clientMessageId);
}
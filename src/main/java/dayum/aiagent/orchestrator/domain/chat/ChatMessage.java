package dayum.aiagent.orchestrator.domain.chat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message",
    indexes = {
        @Index(name = "idx_member_created", columnList = "member_id, chat_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_client", columnNames = {"member_id","client_message_id"})
    })
public class ChatMessage {

  public enum Role { USER, SYSTEM }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  private Long chatId;

  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Role role;

  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String content;

  @Column(name = "client_message_id", length = 64)
  private String clientMessageId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    if (createdAt == null) createdAt = LocalDateTime.now();
  }

  // getters/setters
  public Long getChatId() { return chatId; }
  public Long getMemberId() { return memberId; }
  public void setMemberId(Long memberId) { this.memberId = memberId; }
  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  public String getClientMessageId() { return clientMessageId; }
  public void setClientMessageId(String clientMessageId) { this.clientMessageId = clientMessageId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

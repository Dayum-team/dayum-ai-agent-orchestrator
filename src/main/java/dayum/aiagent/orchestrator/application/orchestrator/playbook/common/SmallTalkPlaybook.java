package dayum.aiagent.orchestrator.application.orchestrator.playbook.common;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.ChatPrompt;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmallTalkPlaybook implements Playbook {

  private final ChatClientService chatClientService;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.SMALL_TALK.name())
          .action("사용자의 인사, 감사, 일상적인 대화에 친근하고 자연스럽게 응답")
          .trigger(
              List.of(
                  "인사말 (안녕, 안녕하세요, 하이)",
                  "감사 표현 (고마워, 감사합니다, 땡큐)",
                  "작별 인사 (잘가, 바이, 다음에 봐)",
                  "안부 묻기 (잘 지내?, 어떻게 지내?)",
                  "일상 대화 (오늘 날씨 좋네, 배고파, 뭐해?)"))
          .cautions(
              List.of(
                  "친근하고 자연스러운 톤 유지",
                  "다이어트 도우미 정체성 유지",
                  "대화 후 자연스럽게 서비스로 유도",
                  "시간대별 적절한 인사 (아침/점심/저녁)",
                  "이모지 적절히 활용하여 친근감 표현"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    String response =
        chatClientService.generateResponseMessage(
            reason, context, userMessage, ChatPrompt.SmallTalkPrompt.SYSTEM_MESSAGE);
    return new PlaybookResult(response);
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.SMALL_TALK;
  }
}

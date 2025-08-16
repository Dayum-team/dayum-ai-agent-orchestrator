package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelectionAckPlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      new PlaybookCatalog(
          "SELECTION_ACK",
          "사용자가 제안된 레시피/식단에 만족 표시를 했을 때 긍정적 피드백과 다음 액션 제안",
          List.of(
              "QuickReply로 '이거 좋아요' 선택",
              "긍정적 반응 표현(좋아요, 마음에 들어요, 이걸로 할게요 등)",
              "레시피/식단 선택 확정 의사 표현"),
          List.of(),
          List.of("긍정적 피드백 제공", "다음 가능한 액션 제시"));

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(ConversationContext context, UserMessage userMessage) {
    // TODO : 사용자 레시피 선택 완료시 내려줄 응답, quick replies 추가 등 더 고려필요

    String message = "좋은 선택이에요! 🎉\n\n" + "맛있는 요리가 되길 바라요. 😊\n" + "다음으로 무엇을 도와드릴까요?";
    return new PlaybookResult("✅ 선택 완료", message, null, null);
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.SELECTION_ACK;
  }
}

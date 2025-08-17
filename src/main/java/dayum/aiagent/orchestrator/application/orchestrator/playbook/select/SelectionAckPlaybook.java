package dayum.aiagent.orchestrator.application.orchestrator.playbook.select;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelectionAckPlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.SELECTION_ACK.name())
          .action("사용자가 제안된 레시피/식단에 만족 표시를 했을 때 긍정적 피드백과 다음 액션 제안")
          .requiresContext(List.of(ContextType.PANTRY.name()))
          .trigger(
              List.of(
                  "QuickReply로 '이거 좋아요' 선택",
                  "긍정적 반응 표현(좋아요, 마음에 들어요, 이걸로 할게요 등)",
                  "레시피/식단 선택 확정 의사 표현"))
          .cautions(List.of("긍정적 피드백 제공", "다음 가능한 액션 제시"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(ConversationContext context, UserMessage userMessage) {
    return null;
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.SELECTION_ACK;
  }
}

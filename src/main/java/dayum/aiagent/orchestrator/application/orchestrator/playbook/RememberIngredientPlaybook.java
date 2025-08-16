package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RememberIngredientPlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      new PlaybookCatalog(
          "REMEMBER_INGREDIENT",
          "사용자의 메시지/이미지 입력에서 재료를 추출해 Pantry 에 멱등성을 보장한 Upsert 를 수행",
          List.of("재료를 언급(예: '계란, 양배추가 있어')", "재료가 포함된 이미지 업로드", "사용자가 '추가/저장' 의도를 표현"),
          List.of("중복 재료는 멱등 병합(name 표준화, quantity 누적 규칙 적용)"));

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(ConversationContext context, UserMessage userMessage) {
    return null;
  }
}

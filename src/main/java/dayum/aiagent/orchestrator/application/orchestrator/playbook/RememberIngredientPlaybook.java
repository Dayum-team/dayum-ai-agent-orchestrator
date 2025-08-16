package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import java.util.List;

import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RememberIngredientPlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.REMEMBER_INGREDIENT.name())
          .action("사용자가 보낸 메시지/텍스트/이미지에 포함된 재료를 PANTRY context 에 반영")
          .outputContext(List.of(ContextType.PANTRY.name()))
          .trigger(List.of("음식/재료에 대한 메시지", "사용자가 가지고 있는 재료를 알려주는 경우", "음식/재료가 포함된 이미지를 보내는 경우"))
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
    return PlaybookType.REMEMBER_INGREDIENT;
  }
}

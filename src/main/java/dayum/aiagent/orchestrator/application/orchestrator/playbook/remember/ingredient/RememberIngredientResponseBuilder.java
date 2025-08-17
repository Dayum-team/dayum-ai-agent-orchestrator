package dayum.aiagent.orchestrator.application.orchestrator.playbook.remember.ingredient;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RememberIngredientResponseBuilder {

  private static final String NO_INGREDIENTS_IN_TEXT_TITLE = "🤔 재료를 찾을 수 없어요";
  private static final String NO_INGREDIENTS_IN_TEXT_MESSAGE =
      "메시지에서 재료를 찾을 수 없어요.\n예시: '닭가슴살 200g이랑 브로콜리 있어요'";

  private static final String SUCCESS_TITLE = "🥬 재료 등록 완료";
  private static final String SUCCESS_MESSAGE_FORMAT =
      "✅ 재료를 등록했어요!\n\n%s\n\n총 %d개의 재료가 저장되어 있어요. 이제 레시피를 추천받아보실까요?";

  public PlaybookResult createNoIngredientsResponse() {
    return new PlaybookResult(
        NO_INGREDIENTS_IN_TEXT_TITLE, NO_INGREDIENTS_IN_TEXT_MESSAGE, List.of(), Map.of());
  }

  public PlaybookResult createSuccessResponse(
      List<Ingredient> newIngredients, PantryContext updatedPantry) {

    String ingredientsList =
        newIngredients.stream()
            .map(ing -> String.format("• %s", ing.name()))
            .collect(Collectors.joining("\n"));

    String message =
        String.format(SUCCESS_MESSAGE_FORMAT, ingredientsList, updatedPantry.ingredients().size());

    return new PlaybookResult(
        SUCCESS_TITLE,
        message,
        List.of(),
        Map.of(ContextType.PANTRY, updatedPantry));
  }
}

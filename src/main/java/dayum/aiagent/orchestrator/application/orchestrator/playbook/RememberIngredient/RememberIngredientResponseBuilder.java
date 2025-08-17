package dayum.aiagent.orchestrator.application.orchestrator.playbook.RememberIngredient;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RememberIngredientResponseBuilder {

  private static final String NO_INGREDIENTS_IN_IMAGE_TITLE = "🤔 재료를 찾을 수 없어요";
  private static final String NO_INGREDIENTS_IN_IMAGE_MESSAGE =
      "사진에서 음식이나 식재료를 찾을 수 없어요.\n음식이나 식재료가 잘 보이는 사진을 다시 보내주세요! 📸";

  private static final String NO_INGREDIENTS_IN_TEXT_TITLE = "🤔 재료를 찾을 수 없어요";
  private static final String NO_INGREDIENTS_IN_TEXT_MESSAGE =
      "메시지에서 재료를 찾을 수 없어요.\n예시: '닭가슴살 200g이랑 브로콜리 있어요'";

  private static final String SUCCESS_TITLE = "🥬 재료 등록 완료";
  private static final String SUCCESS_MESSAGE_FORMAT =
      "✅ 재료를 등록했어요!\n\n%s\n\n총 %d개의 재료가 저장되어 있어요. 이제 레시피를 추천받아보실까요?";

  public PlaybookResult createNoIngredientsInImageResponse() {
    return new PlaybookResult(
        NO_INGREDIENTS_IN_IMAGE_TITLE, NO_INGREDIENTS_IN_IMAGE_MESSAGE, List.of(), Map.of());
  }

  public PlaybookResult createNoIngredientsInTextResponse() {
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
        List.of(QuickReply.FIND_ALTERNATIVES, QuickReply.GENERATE_FROM_INGREDIENTS),
        Map.of(ContextType.PANTRY, updatedPantry));
  }
}

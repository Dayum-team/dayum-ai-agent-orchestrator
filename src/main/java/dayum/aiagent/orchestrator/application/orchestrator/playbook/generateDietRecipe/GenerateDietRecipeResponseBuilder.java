package dayum.aiagent.orchestrator.application.orchestrator.playbook.generateDietRecipe;

import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GenerateDietRecipeResponseBuilder {

  public PlaybookResult buildResponse(GeneratedRecipeModels.GeneratedRecipesResponse response) {
    List<GeneratedRecipeModels.GeneratedRecipe> recipes = response.recipes();

    if (recipes.isEmpty()) {
      return createNoResultResponse();
    } else if (recipes.size() == 1) {
      return createSingleRecipeResponse(recipes.get(0), response);
    } else {
      return createMultipleRecipesResponse(recipes, response);
    }
  }

  public PlaybookResult createGenerationFailedResponse() {
    return new PlaybookResult(
        "😅 레시피 생성 실패",
        "죄송해요, 레시피 생성 중 문제가 발생했어요.\n" + "다시 시도해주시거나 다른 방법을 선택해주세요.",
        List.of(QuickReply.GENERATE_FROM_INGREDIENTS, QuickReply.FIND_ALTERNATIVES),
        Map.of());
  }

  private PlaybookResult createSingleRecipeResponse(
      GeneratedRecipeModels.GeneratedRecipe recipe,
      GeneratedRecipeModels.GeneratedRecipesResponse fullResponse) {

    StringBuilder message = new StringBuilder();
    message.append(String.format("✨ **%s**를 만들어드렸어요!\n\n", recipe.title()));

    // 기본 정보
    appendBasicInfo(message, recipe);

    // 재료
    appendIngredients(message, recipe);

    // 조리 과정
    appendCookingSteps(message, recipe);

    // 노트 및 추가 정보
    appendAdditionalInfo(message, recipe, fullResponse);

    message.append("\n\n이 레시피 어떠신가요? 😊");

    return new PlaybookResult(
        "🍳 AI 맞춤 레시피",
        message.toString(),
        List.of(QuickReply.PICK_THIS, QuickReply.FIND_ALTERNATIVES),
        Map.of());
  }

  private PlaybookResult createMultipleRecipesResponse(
      List<GeneratedRecipeModels.GeneratedRecipe> recipes,
      GeneratedRecipeModels.GeneratedRecipesResponse fullResponse) {

    StringBuilder message = new StringBuilder();
    message.append(String.format("✨ %d가지 레시피를 만들어드렸어요!\n\n", recipes.size()));

    for (int i = 0; i < recipes.size(); i++) {
      GeneratedRecipeModels.GeneratedRecipe recipe = recipes.get(i);
      message.append(String.format("**%d. %s**\n", i + 1, recipe.title()));
      message.append(
          String.format("   ⏱️ %d분 | 🔥 %dkcal | ", recipe.timeMinutes(), recipe.caloriesKcal()));
      message.append(
          String.format(
              "단백질 %dg · 탄수화물 %dg · 지방 %dg\n",
              recipe.macros().proteinG(), recipe.macros().carbG(), recipe.macros().fatG()));

      if (i < recipes.size() - 1) {
        message.append("\n");
      }
    }

    message.append("\n어떤 레시피가 마음에 드시나요?");

    return new PlaybookResult(
        "🍳 AI 맞춤 레시피 " + recipes.size() + "종",
        message.toString(),
        List.of(QuickReply.PICK_THIS),
        Map.of());
  }

  private PlaybookResult createNoResultResponse() {
    return new PlaybookResult(
        "😅 레시피 생성 실패",
        "죄송해요, 레시피 생성 중 문제가 발생했어요.\n" + "다시 시도해주시거나 다른 방법을 선택해주세요.",
        List.of(QuickReply.GENERATE_FROM_INGREDIENTS, QuickReply.FIND_ALTERNATIVES),
        Map.of());
  }

  private void appendBasicInfo(
      StringBuilder message, GeneratedRecipeModels.GeneratedRecipe recipe) {
    message.append(
        String.format(
            "⏱️ 조리시간: %d분 | 🍽️ %d인분 | 🔥 %dkcal\n",
            recipe.timeMinutes(), recipe.servings(), recipe.caloriesKcal()));
    message.append(
        String.format(
            "📊 영양성분: 단백질 %dg · 탄수화물 %dg · 지방 %dg\n\n",
            recipe.macros().proteinG(), recipe.macros().carbG(), recipe.macros().fatG()));
  }

  private void appendIngredients(
      StringBuilder message, GeneratedRecipeModels.GeneratedRecipe recipe) {
    message.append("📝 **필요한 재료**\n");
    recipe
        .ingredientsUsed()
        .forEach(ing -> message.append(String.format("- %s %s\n", ing.name(), ing.quantity())));

    if (!recipe.optionalIngredients().isEmpty()) {
      message.append("\n💡 **선택 재료**\n");
      recipe
          .optionalIngredients()
          .forEach(
              opt ->
                  message.append(
                      String.format("- %s %s (%s)\n", opt.name(), opt.quantity(), opt.reason())));
    }
  }

  private void appendCookingSteps(
      StringBuilder message, GeneratedRecipeModels.GeneratedRecipe recipe) {
    message.append("\n👨‍🍳 **조리 과정**\n");
    for (int i = 0; i < recipe.steps().size(); i++) {
      message.append(String.format("%d. %s\n", i + 1, recipe.steps().get(i)));
    }
  }

  private void appendAdditionalInfo(
      StringBuilder message,
      GeneratedRecipeModels.GeneratedRecipe recipe,
      GeneratedRecipeModels.GeneratedRecipesResponse fullResponse) {
    if (recipe.notes() != null && !recipe.notes().isEmpty()) {
      message.append(String.format("\n💬 %s\n", recipe.notes()));
    }

    if (!fullResponse.unusedIngredients().isEmpty()) {
      message.append(
          String.format(
              "\n⚠️ 사용하지 않은 재료: %s", String.join(", ", fullResponse.unusedIngredients())));
    }
  }
}

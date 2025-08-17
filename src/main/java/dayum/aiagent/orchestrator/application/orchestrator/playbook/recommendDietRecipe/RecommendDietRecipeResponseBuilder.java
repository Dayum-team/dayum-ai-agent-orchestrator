package dayum.aiagent.orchestrator.application.orchestrator.playbook.recommendDietRecipe;

import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.tools.dietrecipe.model.RecommendDietRecipeResponse;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendDietRecipeResponseBuilder {

  public PlaybookResult buildResponse(
      List<RecommendDietRecipeResponse> recipes, PantryContext pantryContext) {
    if (recipes.isEmpty()) {
      return createNoResultResponse(pantryContext);
    } else if (recipes.size() == 1) {
      return createSingleResultResponse(recipes.get(0), pantryContext);
    } else {
      return createMultipleResultsResponse(recipes);
    }
  }

  public PlaybookResult createNoPantryResponse() {
    return new PlaybookResult(
        "🥺 재료가 없어요",
        "아직 등록된 재료가 없네요!\n" + "먼저 보유하신 재료를 알려주시면 맞춤 레시피를 추천해드릴게요.",
        List.of(),
        Map.of());
  }

  private PlaybookResult createNoResultResponse(PantryContext pantryContext) {
    String ingredientsList =
        pantryContext.ingredients().stream()
            .limit(3)
            .map(Ingredient::name)
            .collect(Collectors.joining(", "));

    return new PlaybookResult(
        "😢 레시피를 찾을 수 없어요",
        String.format(
            "현재 재료(%s)로 만들 수 있는 레시피를 찾지 못했어요.\n" + "다른 재료를 추가하거나, AI가 새로운 레시피를 만들어드릴까요?",
            ingredientsList),
        List.of(QuickReply.GENERATE_FROM_INGREDIENTS),
        Map.of());
  }

  private PlaybookResult createSingleResultResponse(
      RecommendDietRecipeResponse recipe, PantryContext pantryContext) {

    String usedIngredients = formatIngredients(pantryContext.ingredients(), 3);
    StringBuilder message = new StringBuilder();

    message.append(String.format("🎉 %s로 만들 수 있는 레시피를 찾았어요!\n\n", usedIngredients));

    appendThumbnail(message, recipe);
    appendNutritionInfo(message, recipe);
    appendIngredientsList(message, recipe);

    message.append("\n이걸로 진행할까요?");

    return new PlaybookResult(
        "🍳 딱 맞는 레시피 발견!",
        message.toString(),
        List.of(QuickReply.PICK_THIS, QuickReply.FIND_ALTERNATIVES),
        Map.of());
  }

  private PlaybookResult createMultipleResultsResponse(List<RecommendDietRecipeResponse> recipes) {
    StringBuilder message = new StringBuilder();
    message.append("찾은 레시피들이에요! 어떤 걸 만들어볼까요?\n\n");

    int displayCount = Math.min(recipes.size(), 5);

    for (int i = 0; i < displayCount; i++) {
      RecommendDietRecipeResponse recipe = recipes.get(i);

      if (recipe.thumbnailUrl() != null && !recipe.thumbnailUrl().isEmpty()) {
        message.append(String.format("![레시피 %d](%s)\n", i + 1, recipe.thumbnailUrl()));
      }

      message.append(String.format("**%d번** - %.0fkcal", i + 1, recipe.calories()));

      if (i < displayCount - 1) {
        message.append("\n\n");
      }
    }

    return new PlaybookResult(
        "🍳 레시피를 선택해주세요", message.toString(), List.of(QuickReply.PICK_THIS), Map.of());
  }

  private String formatIngredients(List<Ingredient> ingredients, int limit) {
    return ingredients.stream()
        .limit(limit)
        .map(Ingredient::name)
        .collect(Collectors.joining(", "));
  }

  private void appendThumbnail(StringBuilder message, RecommendDietRecipeResponse recipe) {
    if (recipe.thumbnailUrl() != null && !recipe.thumbnailUrl().isEmpty()) {
      message.append(String.format("![레시피 이미지](%s)\n\n", recipe.thumbnailUrl()));
    }
  }

  private void appendNutritionInfo(StringBuilder message, RecommendDietRecipeResponse recipe) {
    message.append(String.format("🔥 칼로리: %.0fkcal\n", recipe.calories()));
    message.append(
        String.format(
            "📊 영양성분: 탄수화물 %.0fg · 단백질 %.0fg · 지방 %.0fg\n\n",
            recipe.carbohydrates(), recipe.proteins(), recipe.fats()));
  }

  private void appendIngredientsList(StringBuilder message, RecommendDietRecipeResponse recipe) {
    if (!recipe.ingredients().isEmpty()) {
      message.append("📝 필요한 재료:\n");
      recipe
          .ingredients()
          .forEach(ing -> message.append(String.format("- %s %s\n", ing.name(), ing.quantity())));
      message.append("\n");
    }
  }
}

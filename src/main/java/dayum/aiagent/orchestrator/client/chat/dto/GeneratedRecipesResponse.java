package dayum.aiagent.orchestrator.client.chat.dto;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record GeneratedRecipesResponse(
    List<GeneratedRecipe> recipes, List<String> unusedIngredients, List<String> assumptions) {

  public record GeneratedRecipe(
      String title,
      String description,
      int servings,
      int timeMinutes,
      int caloriesKcal,
      int proteinG,
      int carbG,
      int fatG,
      List<Ingredient> ingredientsUsed,
      List<OptionalIngredient> optionalIngredients,
      List<String> steps,
      String notes) {}

  public record OptionalIngredient(String name, String quantity, String reason) {}
}

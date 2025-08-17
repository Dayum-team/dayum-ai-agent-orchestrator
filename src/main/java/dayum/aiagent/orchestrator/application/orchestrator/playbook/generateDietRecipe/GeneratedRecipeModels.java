package dayum.aiagent.orchestrator.application.orchestrator.playbook.generateDietRecipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import dayum.aiagent.orchestrator.common.vo.Ingredient;

import java.util.List;

public class GeneratedRecipeModels {

  public record GeneratedRecipesResponse(
      List<GeneratedRecipe> recipes, List<String> unusedIngredients, List<String> assumptions) {

    public GeneratedRecipesResponse {
      if (unusedIngredients == null) unusedIngredients = List.of();
      if (assumptions == null) assumptions = List.of();
    }
  }

  public record GeneratedRecipe(
      String title,
      int servings,
      @JsonProperty("time_minutes") int timeMinutes,
      @JsonProperty("calories_kcal") int caloriesKcal,
      Macros macros,
      @JsonProperty("ingredients_used") List<Ingredient> ingredientsUsed,
      @JsonProperty("optional_ingredients") List<OptionalIngredient> optionalIngredients,
      List<String> steps,
      String notes) {}

  public record Macros(
      @JsonProperty("protein_g") int proteinG,
      @JsonProperty("carb_g") int carbG,
      @JsonProperty("fat_g") int fatG) {}

  public record OptionalIngredient(String name, String quantity, String reason) {}
}

package dayum.aiagent.orchestrator.application.context.model;

import java.util.ArrayList;
import java.util.List;

import dayum.aiagent.orchestrator.common.vo.Ingredient;

public record RecommendedRecipeContext(List<RecommendedRecipe> recipes) implements ContextValue {

  @Override
  public ContextValue merge(ContextValue newValue) {
    if (newValue instanceof RecommendedRecipeContext) {
      var mergedRecipes = new ArrayList<>(this.recipes);
      mergedRecipes.addAll(((RecommendedRecipeContext) newValue).recipes);
      return new RecommendedRecipeContext(mergedRecipes);
    }
    return this;
  }

  public record RecommendedRecipe(
      String title,
      String description,
      RecipeSource source,
      List<Ingredient> ingredients,
      double calories,
      double carbohydrates,
      double proteins,
      double fats) {}

  public enum RecipeSource {
    DAYUM_RECOMMEND,
    LLM_GENERATE,
  }
}

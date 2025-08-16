package dayum.aiagent.orchestrator.application.tools.dietrecipe.model;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record RecommendDietRecipeResponse(
    String thumbnailUrl,
    String url,
    List<Ingredient> ingredients,
    double calories,
    double carbohydrates,
    double proteins,
    double fats) {}

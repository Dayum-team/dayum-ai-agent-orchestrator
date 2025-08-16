package dayum.aiagent.orchestrator.application.tools.model.response;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record RecipeContentsRecommendResponse(
    String thumbnailUrl,
    String url,
    List<Ingredient> ingredients,
    double calories,
    double carbohydrates,
    double proteins,
    double fats) {}

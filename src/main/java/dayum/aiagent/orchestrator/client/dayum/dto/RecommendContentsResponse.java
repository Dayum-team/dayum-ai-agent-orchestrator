package dayum.aiagent.orchestrator.client.dayum.dto;

import java.util.List;

public record RecommendContentsResponse(
	String title,
	String description,
    String thumbnailUrl,
    String url,
    List<IngredientResponse> ingredients,
    double calories,
    double carbohydrates,
    double proteins,
    double fats) {}

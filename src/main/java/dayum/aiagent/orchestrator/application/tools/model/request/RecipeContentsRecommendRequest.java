package dayum.aiagent.orchestrator.application.tools.model.request;

import java.util.List;

import dayum.aiagent.orchestrator.application.tools.model.vo.Ingredient;

public record RecipeContentsRecommendRequest(List<Ingredient> ingredients) implements ToolRequest {}

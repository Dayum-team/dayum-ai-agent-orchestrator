package dayum.aiagent.orchestrator.application.tools.dietrecipe.model;

import java.util.List;

import dayum.aiagent.orchestrator.application.tools.ToolRequest;
import dayum.aiagent.orchestrator.common.vo.Ingredient;

public record RecommendDietRecipesRequest(List<Ingredient> ingredients) implements ToolRequest {}

package dayum.aiagent.orchestrator.application.tools.dietrecipe.model;

import dayum.aiagent.orchestrator.application.tools.ToolRequest;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record GenerateDietRecipesRequest(List<Ingredient> ingredients) implements ToolRequest {}

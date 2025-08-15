package dayum.aiagent.orchestrator.application.tools.model.request;

import dayum.aiagent.orchestrator.application.tools.model.vo.Ingredient;
import java.util.List;

public record RecipeGenerateRequest(List<Ingredient> ingredients) implements ToolRequest {}

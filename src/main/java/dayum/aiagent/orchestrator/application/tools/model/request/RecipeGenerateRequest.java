package dayum.aiagent.orchestrator.application.tools.model.request;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record RecipeGenerateRequest(List<Ingredient> ingredients) implements ToolRequest {}

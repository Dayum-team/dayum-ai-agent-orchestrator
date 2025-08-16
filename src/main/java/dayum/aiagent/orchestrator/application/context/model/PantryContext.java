package dayum.aiagent.orchestrator.application.context.model;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record PantryContext(List<Ingredient> ingredients) implements ContextValue {}

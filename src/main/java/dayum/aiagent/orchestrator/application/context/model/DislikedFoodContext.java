package dayum.aiagent.orchestrator.application.context.model;

import java.util.List;

public record DislikedFoodContext(List<String> dislikedFoods) implements ContextValue {}

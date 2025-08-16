package dayum.aiagent.orchestrator.application.context.model;

import java.util.List;

public record DislikeFoodContext(List<String> dislikeFoods) implements ContextValue {}

package dayum.aiagent.orchestrator.application.context.model;

public sealed interface ContextValue permits PantryContext, RecommendedRecipeContext, TasteAttributeContext {

  ContextValue merge(ContextValue newValue);
}

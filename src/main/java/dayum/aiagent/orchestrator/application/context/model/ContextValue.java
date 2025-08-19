package dayum.aiagent.orchestrator.application.context.model;

public sealed interface ContextValue permits TasteAttributeContext, PantryContext {

  ContextValue merge(ContextValue newValue);
}

package dayum.aiagent.orchestrator.application.context.model;

import java.util.List;

public record TasteAttributeContext(List<String> attributes) implements ContextValue {

  @Override
  public ContextValue merge(ContextValue newValue) {
    return null;
  }
}

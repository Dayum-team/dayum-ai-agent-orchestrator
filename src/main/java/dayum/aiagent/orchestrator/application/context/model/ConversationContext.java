package dayum.aiagent.orchestrator.application.context.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class ConversationContext {

  private final long memberId;
  private final String sessionId;
  private Map<ContextType, ContextValue> contexts;
  private final List<ShortTermContext> shortTermContexts;
  private final String rollingSummary;

  public void merge(Map<ContextType, ContextValue> newContexts) {
    if (newContexts == null || newContexts.isEmpty()) {
      return;
    }
    var mergedContexts = new HashMap<ContextType, ContextValue>();
    newContexts.forEach(
        (type, newValue) -> {
          var context = this.contexts.get(type);
          if (context == null) {
            mergedContexts.put(type, newValue);
          } else {
            mergedContexts.put(type, context.merge(newValue));
          }
        });
    this.contexts.putAll(mergedContexts);
  }

  public Map<ContextType, ContextValue> contexts() {
    return this.contexts;
  }

  public List<ShortTermContext> shortTermContexts() {
    return this.shortTermContexts;
  }

  public String rollingSummary() {
    return this.rollingSummary;
  }
}

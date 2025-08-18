package dayum.aiagent.orchestrator.application.context.port;

import java.util.Map;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;

public interface DomainContextRepository {

  Map<ContextType, ContextValue> fetchBy(String sessionId);

  void update(String sessionId, Map<ContextType, ContextValue> contexts);
}

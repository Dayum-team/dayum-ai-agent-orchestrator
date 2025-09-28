package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.port.DomainContextRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DomainContextInMemoryRepository implements DomainContextRepository {

  private final AtomicReference<Map<String, Map<ContextType, ContextValue>>> contexts =
      new AtomicReference<>(new HashMap<>());

  @Override
  public Map<ContextType, ContextValue> fetchBy(String sessionId) {
    return this.contexts.get().getOrDefault(sessionId, new HashMap<>());
  }

  @Override
  public void update(String sessionId, Map<ContextType, ContextValue> contexts) {
    this.contexts.get().put(sessionId, contexts);
  }
}

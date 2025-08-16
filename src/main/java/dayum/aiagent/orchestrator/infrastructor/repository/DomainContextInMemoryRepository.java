package dayum.aiagent.orchestrator.infrastructor.repository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Repository;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.port.DomainContextRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DomainContextInMemoryRepository implements DomainContextRepository {

  private final AtomicReference<Map<String, Map<ContextType, ContextValue>>> contexts =
      new AtomicReference<>();

  @Override
  public Map<ContextType, ContextValue> fetchBy(String sessionId) {
    return contexts.get().getOrDefault(sessionId, Map.of());
  }
}

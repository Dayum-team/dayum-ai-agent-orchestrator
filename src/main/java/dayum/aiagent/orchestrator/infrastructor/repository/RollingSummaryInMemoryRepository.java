package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.application.context.port.RollingSummaryRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class RollingSummaryInMemoryRepository implements RollingSummaryRepository {

  private final AtomicReference<Map<String, String>> rollingSummaries =
      new AtomicReference<>(new HashMap<>());

  @Override
  public String fetchBy(String sessionId) {
    return rollingSummaries.get().getOrDefault(sessionId, "");
  }

  @Override
  public void update(String sessionId, String rollingSummary) {
    rollingSummaries.get().put(sessionId, rollingSummary);
  }
}

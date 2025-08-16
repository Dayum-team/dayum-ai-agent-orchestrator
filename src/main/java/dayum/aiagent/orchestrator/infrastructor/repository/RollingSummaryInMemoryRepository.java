package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.application.context.port.RollingSummaryRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RollingSummaryInMemoryRepository implements RollingSummaryRepository {

  private final Map<String, String> rollingSummaries = new HashMap<>();

  @Override
  public String fetchBy(String sessionId) {
    return rollingSummaries.getOrDefault(sessionId, "");
  }

  @Override
  public void update(String sessionId, String rollingSummary) {
    rollingSummaries.put(sessionId, rollingSummary);
  }
}

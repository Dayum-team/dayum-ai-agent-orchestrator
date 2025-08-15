package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.application.context.port.RollingSummaryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RollingSummaryJpaRepository implements RollingSummaryRepository {

  @Override
  public String fetchBy(String sessionId) {
    return "";
  }

  @Override
  public void update(String sessionId, String rollingSummary) {}
}

package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;
import dayum.aiagent.orchestrator.application.context.port.ShortTermContextRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ShortTermContextRedisRepository implements ShortTermContextRepository {

  @Override
  public List<ShortTermContext> fetchBy(String sessionId) {
    return List.of();
  }

  @Override
  public void append(String sessionId, ShortTermContext newContext) {}
}

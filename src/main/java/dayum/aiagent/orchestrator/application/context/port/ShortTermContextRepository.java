package dayum.aiagent.orchestrator.application.context.port;

import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;
import java.util.List;

public interface ShortTermContextRepository {

  List<ShortTermContext> fetchBy(String sessionId);
}

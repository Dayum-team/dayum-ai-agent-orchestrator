package dayum.aiagent.orchestrator.application.context.port;

import dayum.aiagent.orchestrator.application.context.model.ShortTermContext;
import java.util.List;

public interface ShortTermContextRepository {

  List<ShortTermContext> fetchBy(String sessionId);

  void append(String sessionId, ShortTermContext newContext);
}

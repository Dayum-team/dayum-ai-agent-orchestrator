package dayum.aiagent.orchestrator.application.context.port;

import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;

public interface RollingSummaryRepository {

  String fetchBy(String sessionId);

  void update(String sessionId, String beforeRollingSummary, ShortTermContext newContext);
}

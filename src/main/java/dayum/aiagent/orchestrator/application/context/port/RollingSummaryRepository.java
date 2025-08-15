package dayum.aiagent.orchestrator.application.context.port;


public interface RollingSummaryRepository {

  String fetchBy(String sessionId);

  void update(String sessionId, String rollingSummary);
}

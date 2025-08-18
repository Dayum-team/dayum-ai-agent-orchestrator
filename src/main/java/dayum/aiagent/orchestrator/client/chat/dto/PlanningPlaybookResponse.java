package dayum.aiagent.orchestrator.client.chat.dto;

import java.util.List;

public record PlanningPlaybookResponse(List<Step> steps) {

  public record Step(String playbookId, String reason, int priority) {}
}

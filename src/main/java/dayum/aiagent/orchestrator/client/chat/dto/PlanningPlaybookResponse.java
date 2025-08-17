package dayum.aiagent.orchestrator.client.chat.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PlanningPlaybookResponse(List<Step> steps) {

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record Step(String playbookId, String reason, int priority) {}
}

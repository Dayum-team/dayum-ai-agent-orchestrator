package dayum.aiagent.orchestrator.application.orchestrator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
  private String toolName;

  private String toolArguments;

  private String reasoning;

  private boolean requiresToolExecution;

  private boolean isFinalAnswer;
}

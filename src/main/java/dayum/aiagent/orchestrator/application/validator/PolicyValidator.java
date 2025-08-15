package dayum.aiagent.orchestrator.application.validator;

import org.springframework.stereotype.Component;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.Plan;

@Component
public class PolicyValidator implements Validator {

  @Override
  public ValidationResult validate(Plan plan, String toolResult, ConversationContext context) {
    return null;
  }
}

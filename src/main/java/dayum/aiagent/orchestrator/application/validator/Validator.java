package dayum.aiagent.orchestrator.application.validator;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.Plan;

public interface Validator {
  ValidationResult validate(Plan plan, String toolResult, ConversationContext context);
}

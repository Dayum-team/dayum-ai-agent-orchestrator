package dayum.aiagent.orchestrator.application.validator;

import java.util.Map;

public class ValidationResult {
  private boolean isValid;
  private String failureReason;
  private String validatorType;
  private Map<String, String> feedback;
}

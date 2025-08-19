package dayum.aiagent.orchestrator.application.validator.config;

import dayum.aiagent.orchestrator.application.validator.ValidatorType;

public interface ValidatorConfig {
  String getSystemMessage();

  ValidatorType getValidatorType();
}

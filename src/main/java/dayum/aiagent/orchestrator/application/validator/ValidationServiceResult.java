package dayum.aiagent.orchestrator.application.validator;

import lombok.Builder;
import java.util.List;

@Builder
public record ValidationServiceResult(boolean isValid, List<ValidatorResult> results) {
  @Builder
  public record ValidatorResult(ValidatorType validatorType, boolean isValid, String reason) {}
}

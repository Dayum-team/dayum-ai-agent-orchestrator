package dayum.aiagent.orchestrator.application.validator;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidatorService {
  private final List<Validator> validators;

  public String validate(String answer) {
    return "";
  }
}

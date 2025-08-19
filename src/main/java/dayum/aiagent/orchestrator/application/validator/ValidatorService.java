package dayum.aiagent.orchestrator.application.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.validator.config.ValidatorConfig;
import dayum.aiagent.orchestrator.client.chat.ModelType;
import dayum.aiagent.orchestrator.client.chat.clova.ClovaStudioChatClient;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidatorService {

  private final List<ValidatorConfig> validatorConfigs;
  private final ClovaStudioChatClient chatClientService;
  private final ObjectMapper objectMapper;

  public ValidationServiceResult validate(
      ConversationContext context, List<PlaybookResult> playbookResults) {

    List<ValidationServiceResult.ValidatorResult> results = new ArrayList<>();
    boolean allValid = true;

    for (ValidatorConfig config : validatorConfigs) {
      ValidationServiceResult.ValidatorResult result =
          validateWithConfig(config, context, playbookResults);
      results.add(result);

      if (!result.isValid()) {
        allValid = false;
        log.warn(
            "Validation failed - Type: {}, Reason: {}", result.validatorType(), result.reason());
      }
    }

    return ValidationServiceResult.builder().isValid(allValid).results(results).build();
  }

  private ValidationServiceResult.ValidatorResult validateWithConfig(
      ValidatorConfig config, ConversationContext context, List<PlaybookResult> playbookResults) {

    try {
      PlaybookResult lastResult = playbookResults.getLast();

      String userMessage = buildUserMessage(lastResult);

      ChatCompletionResponse response =
          chatClientService.chatCompletion(
              config.getSystemMessage(), userMessage, context, ModelType.HCX_005);

      ValidationResponse validationResponse = parseValidationResponse(response.message());

      return ValidationServiceResult.ValidatorResult.builder()
          .validatorType(config.getValidatorType())
          .isValid(validationResponse.isValid())
          .reason(validationResponse.reason())
          .build();

    } catch (Exception e) {
      log.error("Validation failed for {}", config.getValidatorType(), e);
      return ValidationServiceResult.ValidatorResult.builder()
          .validatorType(config.getValidatorType())
          .isValid(false)
          .reason("검증 중 오류 발생: " + e.getMessage())
          .build();
    }
  }

  private String buildUserMessage(PlaybookResult result) {
    return String.format(
        """
			다음 PlaybookResult를 검증해주세요:

			Title: %s
			Message: %s
			QuickReplies: %s
			Output: %s
			""",
        result.title(), result.message(), result.quickReplies(), result.output());
  }

  private ValidationResponse parseValidationResponse(String message) {
    try {
      return objectMapper.readValue(message, ValidationResponse.class);
    } catch (Exception e) {
      log.error("Failed to parse validation response as JSON: {}", message, e);
      return new ValidationResponse(false, "LLM 응답 파싱 실패: " + message);
    }
  }
}

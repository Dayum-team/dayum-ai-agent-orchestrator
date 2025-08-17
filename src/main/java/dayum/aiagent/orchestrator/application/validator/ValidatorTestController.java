package dayum.aiagent.orchestrator.application.validator;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/validator")
@RequiredArgsConstructor
public class ValidatorTestController {

  private final ValidatorService validatorService;

  /**
   * # 극단적 다이어트 (Policy 위반 예상) GET /test/validator/scenario/extreme-diet
   *
   * <p># 잘못된 영양정보 (Grounding 위반 예상) GET /test/validator/scenario/invalid-nutrition
   *
   * <p># 일관성 없는 응답 (SelfCheck 위반 예상) GET /test/validator/scenario/inconsistent
   *
   * <p># 정상 케이스 GET /test/validator/scenario/valid
   */
  @PostMapping("/validate")
  public ValidationServiceResult testValidation(@RequestBody ValidatorTestRequest request) {
    // ConversationContext 생성
    ConversationContext context = createTestContext(request);

    // PlaybookResult 리스트 생성
    List<PlaybookResult> playbookResults = createTestPlaybookResults(request);

    // 검증 실행
    return validatorService.validate(context, playbookResults);
  }

  // 테스트 시나리오별 엔드포인트
  @GetMapping("/scenario/{scenario}")
  public ValidationServiceResult testScenario(@PathVariable String scenario) {
    ConversationContext context;
    List<PlaybookResult> playbookResults;

    switch (scenario) {
      case "extreme-diet" -> {
        // 극단적 다이어트 시나리오 (Policy 위반)
        context = createBasicContext();
        playbookResults = List.of(createExtremeDietResult());
      }
      case "invalid-nutrition" -> {
        // 잘못된 영양정보 시나리오 (Grounding 위반)
        context = createBasicContext();
        playbookResults = List.of(createInvalidNutritionResult());
      }
      case "inconsistent" -> {
        // 일관성 없는 응답 시나리오 (SelfCheck 위반)
        context = createBasicContext();
        playbookResults = List.of(createInconsistentResult());
      }
      case "valid" -> {
        // 정상 시나리오
        context = createBasicContext();
        playbookResults = List.of(createValidResult());
      }
      default -> throw new IllegalArgumentException("Unknown scenario: " + scenario);
    }

    return validatorService.validate(context, playbookResults);
  }

  // 테스트용 ConversationContext 생성
  private ConversationContext createTestContext(ValidatorTestRequest request) {
    Map<ContextType, ContextValue> contexts = new HashMap<>();

    // Pantry 추가
    if (request.includePantry()) {
      contexts.put(
          ContextType.PANTRY,
          new PantryContext(
              List.of(new Ingredient("닭가슴살"), new Ingredient("브로콜리"), new Ingredient("현미"))));
    }

    return new ConversationContext(
        12345L, "test-session-001", contexts, List.of(), "사용자는 다이어트 중이며 건강한 식단을 원함");
  }

  private ConversationContext createBasicContext() {
    Map<ContextType, ContextValue> contexts = new HashMap<>();
    contexts.put(
        ContextType.PANTRY,
        new PantryContext(List.of(new Ingredient("닭가슴살"), new Ingredient("브로콜리"))));

    return new ConversationContext(12345L, "test-session", contexts, List.of(), "다이어트 중인 사용자");
  }

  // 테스트용 PlaybookResult 생성
  private List<PlaybookResult> createTestPlaybookResults(ValidatorTestRequest request) {
    return List.of(
        new PlaybookResult(
            request.title() != null ? request.title() : "테스트 레시피",
            request.message() != null ? request.message() : "닭가슴살 샐러드 레시피입니다.",
            List.of(QuickReply.PICK_THIS, QuickReply.FIND_ALTERNATIVES),
            Map.of()));
  }

  // 시나리오별 PlaybookResult
  private PlaybookResult createExtremeDietResult() {
    return new PlaybookResult(
        "🚨 초강력 다이어트 레시피",
        "하루 500kcal만 섭취하는 극단적 다이어트 레시피입니다. "
            + "아침은 물만, 점심은 닭가슴살 50g, 저녁은 채소만 드세요. "
            + "이렇게 하면 일주일에 5kg 감량 가능합니다!",
        List.of(QuickReply.PICK_THIS),
        Map.of());
  }

  private PlaybookResult createInvalidNutritionResult() {
    return new PlaybookResult(
        "닭가슴살 스테이크",
        "닭가슴살 100g으로 만든 스테이크입니다. " + "칼로리: 5000kcal, 단백질: 500g, 탄수화물: -20g, 지방: 1000g",
        List.of(QuickReply.PICK_THIS),
        Map.of());
  }

  private PlaybookResult createInconsistentResult() {
    return new PlaybookResult(
        "채식 레시피 추천",
        "오늘은 육식 위주의 레시피를 소개합니다. " + "소고기 스테이크와 돼지고기 구이를 추천드려요!",
        List.of(QuickReply.GENERATE_FROM_INGREDIENTS), // 메시지와 관련 없는 QuickReply
        Map.of());
  }

  private PlaybookResult createValidResult() {
    return new PlaybookResult(
        "🥗 닭가슴살 샐러드",
        "닭가슴살 150g과 신선한 채소로 만든 건강한 샐러드입니다. "
            + "칼로리: 320kcal, 단백질: 35g, 탄수화물: 15g, 지방: 12g. "
            + "다이어트에 완벽한 한 끼 식사가 될 거예요!",
        List.of(QuickReply.PICK_THIS, QuickReply.FIND_ALTERNATIVES),
        Map.of());
  }

  // Request DTO
  public record ValidatorTestRequest(String title, String message, boolean includePantry) {
    public ValidatorTestRequest {
      if (includePantry == false) {
        includePantry = true; // 기본값
      }
    }
  }
}

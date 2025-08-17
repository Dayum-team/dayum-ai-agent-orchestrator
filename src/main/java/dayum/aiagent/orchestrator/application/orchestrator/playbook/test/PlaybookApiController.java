package dayum.aiagent.orchestrator.application.orchestrator.playbook.test;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.generateDietRecipe.GenerateDietRecipePlaybook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.recommendDietRecipe.RecommendDietRecipePlaybook;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test") // API 기본 경로 설정
@RequiredArgsConstructor
public class PlaybookApiController {

  // 생성자 주입을 통해 GenerateDietRecipePlaybook 빈(Bean)을 가져옵니다.
  private final GenerateDietRecipePlaybook generateDietRecipePlaybook;
  private final RecommendDietRecipePlaybook recommendDietRecipePlaybook;

  @PostMapping("/generate-recipe")
  public ResponseEntity<PlaybookResult> testGenerateRecipePlaybook(
      @RequestBody GenerateRecipeRequest request) {

    // 1. 요청 DTO로부터 ConversationContext와 UserMessage를 생성합니다.
    // DTO의 IngredientDto 리스트를 도메인 객체인 Ingredient 리스트로 변환합니다.
    List<Ingredient> ingredients =
        request.ingredients().stream()
            .map(dto -> new Ingredient(dto.name(), dto.quantity()))
            .collect(Collectors.toList());

    PantryContext pantryContext = new PantryContext(ingredients);
    Map<ContextType, ContextValue> contexts = new HashMap<>();
    contexts.put(ContextType.PANTRY, pantryContext);

    ConversationContext context =
        new ConversationContext(
            request.memberId(),
            request.sessionId(),
            contexts,
            new ArrayList<>(), // 단기 컨텍스트는 비워둡니다.
            "API 테스트를 통해 대화 요약이 생성됨.");

    UserMessage userMessage = new UserMessage(request.userMessage(), null, null);

    // 2. 플레이북의 play 메서드를 호출합니다.
    PlaybookResult result = generateDietRecipePlaybook.play(context, userMessage);

    // 3. 실행 결과를 HTTP 200 OK 응답과 함께 JSON 형태로 반환합니다.
    return ResponseEntity.ok(result);
  }

  @PostMapping("/recommend-recipe")
  public ResponseEntity<PlaybookResult> testRecommendRecipePlaybook(
      @RequestBody RecommendRecipeRequest request) {

    // 1. 요청 DTO로부터 ConversationContext와 UserMessage를 생성합니다.
    List<Ingredient> ingredients =
        request.ingredients().stream()
            .map(dto -> new Ingredient(dto.name(), dto.quantity()))
            .collect(Collectors.toList());

    PantryContext pantryContext = new PantryContext(ingredients);
    Map<ContextType, ContextValue> contexts = new HashMap<>();
    contexts.put(ContextType.PANTRY, pantryContext);

    ConversationContext context =
        new ConversationContext(
            request.memberId(),
            request.sessionId(),
            contexts,
            new ArrayList<>(),
            "API 테스트 (레시피 추천)");

    UserMessage userMessage = new UserMessage(request.userMessage(), null, null);

    // 2. RecommendDietRecipePlaybook의 play 메서드를 호출합니다.
    PlaybookResult result = recommendDietRecipePlaybook.play(context, userMessage);

    // 3. 실행 결과를 HTTP 200 OK 응답으로 반환합니다.
    return ResponseEntity.ok(result);
  }
}

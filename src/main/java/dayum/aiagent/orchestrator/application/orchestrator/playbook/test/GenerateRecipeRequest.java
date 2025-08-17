package dayum.aiagent.orchestrator.application.orchestrator.playbook.test;

import java.util.List;

// API 요청 본문(Request Body)의 구조를 정의하는 레코드
public record GenerateRecipeRequest(
    Long memberId, String sessionId, String userMessage, List<IngredientDto> ingredients) {
  // 요청 데이터에 포함될 재료의 구조를 정의하는 내부 레코드
  public record IngredientDto(String name, String quantity) {}
}

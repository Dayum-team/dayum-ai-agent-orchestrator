package dayum.aiagent.orchestrator.application.orchestrator.playbook.test;

import java.util.List;

public record RecommendRecipeRequest(
    Long memberId, String sessionId, String userMessage, List<IngredientDto> ingredients) {
  // 요청 데이터에 포함될 재료의 구조 정의
  public record IngredientDto(String name, String quantity) {}
}

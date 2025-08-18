package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookPlanResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Orchestrator {

  private final PlaybookPlanner planner;

  public List<PlaybookResult> runTurn(ConversationContext context, UserMessage userMessage) {
    List<PlaybookPlanResult> plans = planner.planning(context, userMessage);
    List<PlaybookResult> results = new ArrayList<>();

    for (PlaybookPlanResult plan : plans) {
      // 1. Playbook이 요구하는 컨텍스트 가져오기
      List<ContextType> requiredContexts = plan.playbook().getRequiresContext();

      // 2. 필수 컨텍스트 검사
      List<ContextType> missingContexts = findMissingContexts(requiredContexts, context.contexts());

      // 필수 컨텍스트가 없는 경우
      if (!missingContexts.isEmpty()) {
        PlaybookResult errorResult =
            createMissingContextResult(plan.playbook().getType(), missingContexts);
        results.add(errorResult);
        break;
      }

      // 3. Playbook 실행
      PlaybookResult result = plan.playbook().play(plan.reason(), context, userMessage);
      results.add(result);

      // 4. 결과의 output을 원본 context에 upsert
      if (result.output() != null) {
        upsertContexts(context.contexts(), result.output());
      }
    }

    // TODO : Validator 거쳐서 최종 응답 반환
    return results;
  }

  /** 컨텍스트 upsert 처리 */
  private void upsertContexts(
      Map<ContextType, ContextValue> currentContexts, Map<ContextType, ContextValue> newContexts) {

    newContexts.forEach(
        (type, newValue) -> {
          if (newValue == null) return;

          if (type == ContextType.PANTRY) {
            currentContexts.merge(
                type,
                newValue,
                (existing, incoming) -> {
                  if (!(existing instanceof PantryContext existingPantry)
                      || !(incoming instanceof PantryContext newPantry)) {
                    return incoming;
                  }

                  // LinkedHashMap으로 순서 유지하면서 중복 제거
                  Map<String, Ingredient> merged = new LinkedHashMap<>();
                  existingPantry.ingredients().forEach(ing -> merged.put(ing.name(), ing));
                  newPantry.ingredients().forEach(ing -> merged.put(ing.name(), ing));

                  return new PantryContext(new ArrayList<>(merged.values()));
                });
          } else {
            currentContexts.put(type, newValue);
          }
        });
  }

  /** 현재 컨텍스트에 없는 필수 컨텍스트 찾기 */
  private List<ContextType> findMissingContexts(
      List<ContextType> requiredContexts, Map<ContextType, ContextValue> currentContexts) {

    if (requiredContexts == null || requiredContexts.isEmpty()) {
      return List.of();
    }

    return requiredContexts.stream()
        .filter(contextType -> !currentContexts.containsKey(contextType))
        .toList();
  }

  /** 필수 컨텍스트 부족 응답 생성 (초안) */
  private PlaybookResult createMissingContextResult(
      PlaybookType playbookType, List<ContextType> missingContexts) {

    StringBuilder message = new StringBuilder();
    message.append("필요한 정보가 부족해요 😅\n\n");

    for (ContextType missing : missingContexts) {
      if (missing == ContextType.PANTRY) {
        message.append("🥬 보유하신 재료를 먼저 알려주세요.\n");
      } else if (missing == ContextType.TASTE_ATTRIBUTE) {
        message.append("😋 음식 취향이나 알러지 정보를 알려주세요.\n");
      }
    }
    return new PlaybookResult("정보 부족", message.toString(), List.of(), Map.of());
  }
}

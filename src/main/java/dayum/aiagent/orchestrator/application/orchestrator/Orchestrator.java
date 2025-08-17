package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookPlanResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.ArrayList;
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
      // 1. 필수 컨텍스트 검증
      List<String> missingContexts = checkRequiredContexts(plan, context.contexts());
      if (!missingContexts.isEmpty()) {
        results.add(createMissingContextResult(missingContexts));
        break;
      }

      // 2. Playbook 실행
      PlaybookResult result = plan.playbook().play(context, userMessage);
      results.add(result);

      // 3. 결과의 output을 원본 context에 병합
      if (result.output() != null) {
        context.contexts().putAll(result.output());
      }
    }

    return results;
  }

  private List<String> checkRequiredContexts(
      PlaybookPlanResult plan, Map<ContextType, ContextValue> currentContexts) {

    return null;
  }

  private PlaybookResult createMissingContextResult(List<String> missingContexts) {
    String message = "필요한 정보가 부족합니다: " + String.join(", ", missingContexts);

    return new PlaybookResult("정보 부족", message, List.of(), Map.of());
  }
}

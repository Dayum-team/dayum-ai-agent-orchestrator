package dayum.aiagent.orchestrator.application.orchestrator;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.ToolRegistry;
import dayum.aiagent.orchestrator.application.validator.ValidationResult;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Orchestrator {
  private final ToolRegistry toolRegistry;
  private final Planner planner;

  // private final List<Validator> validators;

  public String runTurn(ConversationContext context, String userQuery) {
    LoopContext loopContext = new LoopContext();

    while (loopContext.canContinue()) {
      // 1. Tool Schema 조회
      List<ToolSignatureSchema.ToolSchema> schemas = toolRegistry.getToolSchemaList();

      // 2. Plan 요청
      Plan plan = planner.requestPlan(schemas, userQuery, context, loopContext);

      // 3-1. Tool 실행
      if (plan.isRequiresToolExecution()) {
        String toolResult =
            toolRegistry.execute(plan.getToolName(), plan.getToolArguments(), context);

        // 3-2. Tool 실행 결과를 Validator들에게 검증

        // Loop Context에 실행 결과와 검증 결과 추가

        // 검증 실패 시 다시 Loop

        // 최종 답변인 경우
        if (plan.isFinalAnswer()) {
          break;
        }
      }
    }
    return "";
  }
}

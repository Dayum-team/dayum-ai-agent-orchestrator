package dayum.aiagent.orchestrator.application.orchestrator;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.ToolRegistry;
import dayum.aiagent.orchestrator.application.validator.ValidationResult;
import dayum.aiagent.orchestrator.application.validator.ValidatorService;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Orchestrator {
  private final ToolRegistry toolRegistry;
  private final Planner planner;
  private final ValidatorService validatorService;

  public String runTurn(ConversationContext context, String userMessage) {
    LoopContext loopContext = new LoopContext();
    String finalAnswer = null;

    while (loopContext.canContinue()) {
      // 1. Tool Schema 조회
      List<ToolSignatureSchema.ToolSchema> schemas = toolRegistry.getToolSchemaList();

      // 2. Plan 요청
      Plan plan = planner.requestPlan(schemas, userMessage, context, loopContext);

      // 3-1. Tool 실행
      if (plan.isRequiresToolExecution()) {
        String toolResult =
            toolRegistry.execute(plan.getToolName(), plan.getToolArguments(), context);

        // Loop Context에 실행 결과 추가
        loopContext.addToolResult(plan.getToolName(), plan.getToolArguments(), toolResult);

        // 최종 답변인 경우
        if (plan.isFinalAnswer()) {
          break;
        }
      } else {
      }
    }

    return validatorService.validate(finalAnswer);
  }
}

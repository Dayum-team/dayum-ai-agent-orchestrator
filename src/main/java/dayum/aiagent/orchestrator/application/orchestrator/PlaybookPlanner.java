package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookPlanResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningPlaybookResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaybookPlanner {

  private final List<Playbook> playbooks;
  private final ChatClientService chatClientService;

  public List<PlaybookPlanResult> planning(ConversationContext context, UserMessage userMessage) {
    if (userMessage.selectQuickRely() != null) {
      return switch (userMessage.selectQuickRely()) {
        case PICK_THIS -> build(PlaybookType.SELECTION_ACK, "마음에 들어! QuickReply 를 선택해 최종 레시피 선택완료");
        case FIND_ALTERNATIVES, GENERATE_FROM_INGREDIENTS ->
            build(PlaybookType.GENERATE_DIET_RECIPE, "새로운 레시피 생성을 요청");
      };
    }

    var catalogMap =
        playbooks.stream().collect(Collectors.toMap(Playbook::getType, Playbook::getCatalog));
    List<PlanningPlaybookResponse.Step> steps =
        chatClientService.planningSteps(context, userMessage, catalogMap).steps().stream()
            .sorted(Comparator.comparingInt(PlanningPlaybookResponse.Step::priority))
            .toList();
    return switch (steps.size()) {
      case 0 -> this.build(PlaybookType.GUARDRAIL, "계획을 세우지 못해 GUARDRAIL 플레이북을 기본으로 사용");
      default -> this.build(steps);
    };
  }

  private List<PlaybookPlanResult> build(PlaybookType type, String reason) {
    var playbook =
        this.playbooks.stream().filter(p -> p.getType().equals(type)).findFirst().orElseThrow();
    return List.of(new PlaybookPlanResult(type, playbook, reason));
  }

  private List<PlaybookPlanResult> build(List<PlanningPlaybookResponse.Step> steps) {
    return steps.stream()
        .map(
            step -> {
              var type = PlaybookType.valueOf(step.playbookId());
              var playbook =
                  this.playbooks.stream()
                      .filter(p -> p.getType().equals(type))
                      .findFirst()
                      .orElseThrow();
              return new PlaybookPlanResult(type, playbook, step.reason());
            })
        .toList();
  }
}

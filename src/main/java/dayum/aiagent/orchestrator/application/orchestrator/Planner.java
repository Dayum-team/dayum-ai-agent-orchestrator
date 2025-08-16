package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningPlaybookResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Planner {

  private final List<Playbook> playbooks;
  private final ChatClientService chatClientService;

  public List<Pair<Playbook, String>> planning(
      ConversationContext context, UserMessage userMessage) {
    if (userMessage.selectQuickRely() != null) {
      var playbook =
          switch (userMessage.selectQuickRely()) {
            case PICK_THIS ->
                Pair.of(PlaybookType.SELECTION_ACK, "마음에 들어! QuickReply 를 선택해 최종 레시피 선택완료");
            case FIND_ALTERNATIVES, GENERATE_FROM_INGREDIENTS ->
                Pair.of(PlaybookType.GENERATE_DIET_RECIPE, "새로운 레시피 생성을 요청");
          };
      return List.of(this.selectPlaybook(playbook));
    }

    var catalogMap =
        playbooks.stream().collect(Collectors.toMap(Playbook::getType, Playbook::getCatalog));
    List<Pair<PlaybookType, String>> planList =
        chatClientService.planningSteps(context, userMessage, catalogMap).steps().stream()
            .sorted(Comparator.comparingInt(PlanningPlaybookResponse.Step::priority))
            .map(step -> Pair.of(PlaybookType.valueOf(step.playbookId()), step.reason()))
            .toList();
    return switch (planList.size()) {
      case 0 ->
          this.selectPlaybooks(
              List.of(Pair.of(PlaybookType.GUARDRAIL, "계획을 세우지 못해 GUARDRAIL 플레이북을 기본으로 사용")));
      default -> this.selectPlaybooks(planList);
    };
  }

  private Pair<Playbook, String> selectPlaybook(Pair<PlaybookType, String> plan) {
    return Pair.of(
        this.playbooks.stream()
            .filter(playbook -> playbook.getType().equals(plan.getFirst()))
            .findFirst()
            .orElseThrow(),
        plan.getSecond());
  }

  private List<Pair<Playbook, String>> selectPlaybooks(List<Pair<PlaybookType, String>> planList) {
    return planList.stream()
        .map(
            plan ->
                Pair.of(
                    this.playbooks.stream()
                        .filter(playbook -> playbook.getType().equals(plan.getFirst()))
                        .findFirst()
                        .orElseThrow(),
                    plan.getSecond()))
        .toList();
  }
}

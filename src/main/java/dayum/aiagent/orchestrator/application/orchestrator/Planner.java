package dayum.aiagent.orchestrator.application.orchestrator;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningPlaybookResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Planner {

  private final List<Playbook> playbooks;
  private final ChatClientService chatClientService;

  public List<Playbook> planning(ConversationContext context, UserMessage userMessage) {
    if (userMessage.selectQuickRely() != null) {
      return switch (userMessage.selectQuickRely()) {
        case PICK_THIS -> this.selectPlaybooks(List.of(PlaybookType.SELECTION_ACK));
        case FIND_ALTERNATIVES, GENERATE_FROM_INGREDIENTS ->
            this.selectPlaybooks(List.of(PlaybookType.GENERATE_DIET_RECIPE));
      };
    }
    var catalogMap =
        playbooks.stream().collect(Collectors.toMap(Playbook::getType, Playbook::getCatalog));
    List<PlaybookType> plan =
        chatClientService.planningPlaybooks(catalogMap).steps().stream()
            .sorted(Comparator.comparingInt(PlanningPlaybookResponse.Step::priority))
            .map(step -> PlaybookType.valueOf(step.playbookId()))
            .toList();
    return this.selectPlaybooks(plan);
  }

  private List<Playbook> selectPlaybooks(List<PlaybookType> types) {
    return types.stream()
        .map(
            type ->
                this.playbooks.stream()
                    .filter(playbook -> playbook.getType().equals(type))
                    .findFirst()
                    .orElse(null))
        .filter(Objects::nonNull)
        .toList();
  }
}

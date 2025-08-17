package dayum.aiagent.orchestrator.application.orchestrator.playbook.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.ChatPrompt;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShowContextPlaybook implements Playbook {

  private final ChatClientService chatClientService;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.SHOW_CONTEXT.name())
          .action(
              "해당 사용자가 가지고있는 Context 를 사용자에게 제공하기위해 활용. (Context 종류: 지금까지의 대화 요약, 최근 K 개의 대화전문, "
                  + Arrays.stream(ContextType.values()).map(Enum::name).toList()
                  + ")")
          .trigger(List.of("지금까지의 대화 요약", "내가 이야기한 것에 대한 확인 요청", "내가 가지고있는건 뭔지 질문"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    var response =
        chatClientService.generateResponseMessage(
            reason, context, userMessage, ChatPrompt.ShowContextPrompt.SYSTEM_MESSAGE);
    return new PlaybookResult(response);
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.SHOW_CONTEXT;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of();
  }
}

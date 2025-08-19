package dayum.aiagent.orchestrator.application.orchestrator.playbook.remember.tasteattribute;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.TasteAttributeContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.ExtractAttributeResponse;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RememberTasteAttributePlaybook implements Playbook {

  private final ChatClientService chatClientService;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.REMEMBER_TASTE_ATTRIBUTE.name())
          .action("사용자가 언급한 음식 취향과 선호도를 저장하기위해 활용")
          .outputContext(List.of(ContextType.TASTE_ATTRIBUTE.name()))
          .trigger(
              List.of(
                  "음식 선호 표현 (매운 음식 좋아해, 단 거 싫어해)",
                  "특정 재료 선호/비선호 (브로콜리는 못 먹어, 치즈 좋아해)",
                  "요리 스타일 선호 (한식이 좋아, 볶음요리 선호해)",
                  "식감 선호 표현 (바삭한 거 좋아해, 물컹한 건 싫어)",
                  "알러지나 불내증 언급 (유당불내증 있어, 견과류 알러지)"))
          .cautions(List.of("긍정/부정 취향 구분하여 저장", "알러지는 별도로 중요도 높게 관리", "상충되는 취향 입력 시 최신 정보로 업데이트"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    ExtractAttributeResponse response =
        chatClientService.extractAttributeFromText(reason, userMessage.getMessage());
    List<String> attributes = response.attributes();

    String title = "✅ 접수 완료!";
    String message = "알겠습니다. 다음 추천에 참고할게요!";

    if (attributes != null && !attributes.isEmpty()) {
      title = "😋 취향 분석 완료!";
      String formattedAttributes =
          attributes.stream().map(attr -> "[" + attr + "]").collect(Collectors.joining(", "));
      message = formattedAttributes + " 다얌이 취향을 기억했습니다!";
    }

    return new PlaybookResult(
        title,
        message,
        List.of(QuickReply.FIND_ALTERNATIVES, QuickReply.GENERATE_FROM_INGREDIENTS),
        Map.of(ContextType.TASTE_ATTRIBUTE, new TasteAttributeContext(attributes)));
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.REMEMBER_TASTE_ATTRIBUTE;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of();
  }
}

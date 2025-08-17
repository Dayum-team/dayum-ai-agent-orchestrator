package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuardrailPlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.GUARDRAIL.name())
          .action(
              "다이어트, 다이어트 식단, 다이어트 레시피와 관련이 없거나, 안전/정책 위반(자해, 불법, 혐오, 개인정보, 의료 "
                  + "처방 요구 등)에 대한 대화인 경우 대화를 차단하거나 안전한 대안을 제시")
          .trigger(
              List.of(
                  "자해/자살 언급 도움 요청",
                  "불법 행위 조언 요청",
                  "개인정보/민감정보 과다 노출 요구",
                  "전문의 진단/처방 수준 요구",
                  "다이어트와 관련없는 요구",
                  "다이어트 식단와 관련없는 요구",
                  "다이어트 레시피와 관련없는 요구"))
          .cautions(
              List.of(
                  "친절하지만 단호하게 거절",
                  "서비스 범위 명확히 안내",
                  "의료 상담은 전문의 상담 권유",
                  "극단적 다이어트는 건강한 대안 제시",
                  "대화 종료보다는 올바른 방향으로 유도"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(ConversationContext context, UserMessage userMessage) {
    return null;
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.GUARDRAIL;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of();
  }
}

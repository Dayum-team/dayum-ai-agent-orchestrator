package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import java.util.List;

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
      new PlaybookCatalog(
          "GUARDRAIL",
          "서비스 범위를 벗어나거나 부적절한 요청을 필터링하고 적절히 리다이렉트",
          List.of(
              "다이어트/음식과 무관한 질문 (주식 추천해줘, 코딩 알려줘)",
              "의료적 조언 요청 (이 약 먹어도 돼?, 병원 가야해?)",
              "극단적 다이어트 요청 (하루 500kcal만 먹으면 어때?)",
              "부적절한 내용 (욕설, 성적 내용, 폭력적 표현)",
              "개인정보 요청 (너 누가 만들었어?, 사용자 정보 알려줘)",
              "불법적인 요청 (다이어트약 구매처, 불법 약물)"),
          List.of(),
          List.of(
              "친절하지만 단호하게 거절",
              "서비스 범위 명확히 안내",
              "의료 상담은 전문의 상담 권유",
              "극단적 다이어트는 건강한 대안 제시",
              "대화 종료보다는 올바른 방향으로 유도"));

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
}

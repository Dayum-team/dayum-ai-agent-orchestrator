package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import java.util.List;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateDietRecipePlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      new PlaybookCatalog(
          "GENERATE_DIET_RECIPE",
          "사용자의 요구사항에 맞는 다이어트 레시피를 AI로 생성하고 영양정보와 함께 제공",
          List.of(
              "레시피 생성 요청 (만들어줘, 추천해줘, 레시피 알려줘)",
              "특정 재료로 요리 요청",
              "칼로리/영양 조건 명시",
              "식단 유형 언급 (저탄고지 레시피, 단백질 위주)",
              "QuickReply로 '레시피 생성' 선택"),
          List.of(ContextType.PANTRY.name(), ContextType.TASTE_ATTRIBUTE.name()),
          List.of(
              "생성된 레시피는 영양정보 필수 포함",
              "알러지나 식이제한 사항 반드시 체크",
              "현실적이고 실행 가능한 레시피 생성",
              "한국인 입맛에 맞는 레시피 우선 고려",
              "PANTRY, TASTE_ATTRIBUTE를 고려한 레시피"));

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
    return PlaybookType.GENERATE_DIET_RECIPE;
  }
}

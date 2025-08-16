package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateDietRecipePlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.GENERATE_DIET_RECIPE.name())
          .action(
              "사용자가 PANTRY 에 가지고 있는 재료로 만들 수 있으면서, 사용자로부터 주어진 제약조건"
                  + "(칼로리/영양성분/조리시간) 등을 충족하는 다이어트 레시피를 AI로 생성하고 영양정보와 함께 제공")
          .requiresContext(List.of(ContextType.PANTRY.name()))
          .trigger(
              List.of(
                  "칼로리/영양 조건 명시",
                  "식단 유형 언급 (저탄고지 레시피, 단백질 위주)",
                  "QuickReply로 '레시피 생성' 선택",
                  "직접 만들어달라는 사용자 요청",
                  "이미 추천받은 사용자가 또 다른 레시피를 요청",
                  "매운거 싫어, 단백질 높은것 과 같이 사용자 취향을 고려한 추천 요청"))
          .cautions(
              List.of(
                  "반드시 PANTRY 가 USER_CONTEXT_KEY 에 있는경우 선택",
                  "처음 추천해달라는 요청에서 사용 금지",
                  "생성된 레시피는 영양정보 필수 포함",
                  "알러지나 식이제한 사항 반드시 체크",
                  "현실적이고 실행 가능한 레시피 생성",
                  "한국인 입맛에 맞는 레시피 우선 고려",
                  "PANTRY, TASTE_ATTRIBUTE 를 고려한 레시피"))
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
    return PlaybookType.GENERATE_DIET_RECIPE;
  }
}

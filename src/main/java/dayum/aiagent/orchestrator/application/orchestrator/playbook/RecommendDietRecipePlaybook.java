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
public class RecommendDietRecipePlaybook implements Playbook {

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.RECOMMEND_DIET_RECIPE.name())
          .action("PANTRY 에 포함된 사용자가 보유한 재료와 일치하는 기존 레시피 영상을 검색하여 추천")
          .requiresContext(List.of(ContextType.PANTRY.name()))
          .trigger(
              List.of(
                  "직접 만들어달라는 사용자 요청",
                  "이미 추천받은 사용자가 또 다른 레시피를 요청",
                  "매운거 싫어, 단백질 높은것 과 같이 사용자 취향을 고려한 추천 요청"))
          .cautions(
              List.of(
                  "반드시 PANTRY 가 USER_CONTEXT_KEY 에 있는경우 선택",
                  "재료 매칭률이 높은 순으로 정렬",
                  "레시피 영상이 있는 것만 추천",
                  "재료가 부족해도 대체 가능한 레시피 포함"))
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
    return PlaybookType.RECOMMEND_DIET_RECIPE;
  }
}

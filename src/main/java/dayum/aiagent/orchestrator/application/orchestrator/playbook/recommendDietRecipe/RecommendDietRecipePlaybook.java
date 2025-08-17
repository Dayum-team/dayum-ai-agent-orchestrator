package dayum.aiagent.orchestrator.application.orchestrator.playbook.recommendDietRecipe;

import dayum.aiagent.orchestrator.application.context.model.*;
import dayum.aiagent.orchestrator.application.orchestrator.model.*;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.*;
import dayum.aiagent.orchestrator.application.tools.*;
import dayum.aiagent.orchestrator.client.dayum.DayumApiClient;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendDietRecipePlaybook implements Playbook {

  private final DayumApiClient dayumApiClient;
  private final RecommendDietRecipeResponseBuilder responseBuilder;
  private final ObjectMapper objectMapper;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.RECOMMEND_DIET_RECIPE.name())
          .action("PANTRY 에 포함된 사용자가 보유한 재료와 일치하는 기존 레시피 영상을 검색하여 추천")
          .requiresContext(List.of(ContextType.PANTRY.name()))
          .trigger(List.of("다이어트 레시피를 추천해달라는 요청", "가지고있는 재료로 만들 수 있는 레시피 추천 요청"))
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
    PantryContext pantryContext = (PantryContext) context.contexts().get(ContextType.PANTRY);
    // TODO: Pantry 없을때 throws?
    var response = dayumApiClient.recommendContentsBy(pantryContext.ingredients(), 5);
    // TODO: 없을때 Fallback 으로 generate..?
    return responseBuilder.buildResponse(response, pantryContext);
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.RECOMMEND_DIET_RECIPE;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of(ContextType.PANTRY);
  }
}

package dayum.aiagent.orchestrator.application.orchestrator.playbook.dietrecipe;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.context.model.RecommendedRecipeContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.GeneratedRecipesResponse;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningHowToRecommendResponse;
import dayum.aiagent.orchestrator.client.dayum.DayumApiClient;
import dayum.aiagent.orchestrator.client.dayum.dto.IngredientResponse;
import dayum.aiagent.orchestrator.client.dayum.dto.RecommendContentsResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendDietRecipePlaybook implements Playbook {

  private final ChatClientService chatClientService;
  private final DayumApiClient dayumApiClient;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.RECOMMEND_DIET_RECIPE.name())
          .action("PANTRY 에 포함된 사용자가 보유한 재료를 참고해 다이어트 레시피를 추천한다.")
          .requiresContext(List.of(ContextType.PANTRY.name()))
          .trigger(List.of("다이어트 레시피를 추천해달라는 요청", "가지고있는 재료로 만들 수 있는 레시피 추천 요청"))
          .cautions(List.of("반드시 PANTRY 가 USER_CONTEXT_KEY 에 있는경우 선택"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    PantryContext pantryContext = (PantryContext) context.contexts().get(ContextType.PANTRY);
    if (pantryContext == null || pantryContext.ingredients().isEmpty()) {
      throw new RuntimeException();
    }

    PlanningHowToRecommendResponse response =
        chatClientService.planningHowToRecommend(reason, userMessage);
    log.info("PlanningHowToRecommendResponse: {}", response);

    List<RecommendedRecipeContext.RecommendedRecipe> recommendedRecipes =
        switch (response.decision()) {
          case "RECOMMEND" -> {
            var contents = dayumApiClient.recommendContentsBy(pantryContext.ingredients(), 5);
            if (contents.isEmpty()) {
              yield convertGeneratedRecipes(
                  chatClientService
                      .generateDietRecipes(context, pantryContext.ingredients())
                      .recipes());
            }
            yield convertRecommendedRecipes(contents);
          }
          case "GENERATE" ->
              convertGeneratedRecipes(
                  chatClientService
                      .generateDietRecipes(context, pantryContext.ingredients())
                      .recipes());
          default -> Collections.emptyList();
        };
    return new PlaybookResult(
        "레시피 추천 완료",
        recommendedRecipes.toString(),
        List.of(),
        Map.of(ContextType.RECOMMENDED_RECIPE, new RecommendedRecipeContext(recommendedRecipes)));
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.RECOMMEND_DIET_RECIPE;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of(ContextType.PANTRY);
  }

  private List<RecommendedRecipeContext.RecommendedRecipe> convertRecommendedRecipes(
      List<RecommendContentsResponse> recipes) {
    return recipes.stream()
        .map(
            recipe ->
                new RecommendedRecipeContext.RecommendedRecipe(
                    recipe.title(),
                    recipe.description(),
                    RecommendedRecipeContext.RecipeSource.DAYUM_RECOMMEND,
                    recipe.ingredients().stream().map(IngredientResponse::toIngredient).toList(),
                    recipe.calories(),
                    recipe.carbohydrates(),
                    recipe.proteins(),
                    recipe.fats()))
        .toList();
  }

  private List<RecommendedRecipeContext.RecommendedRecipe> convertGeneratedRecipes(
      List<GeneratedRecipesResponse.GeneratedRecipe> recipes) {
    return recipes.stream()
        .map(
            recipe ->
                new RecommendedRecipeContext.RecommendedRecipe(
                    recipe.title(),
                    recipe.description(),
                    RecommendedRecipeContext.RecipeSource.LLM_GENERATE,
                    recipe.ingredientsUsed(),
                    recipe.caloriesKcal(),
                    recipe.carbG(),
                    recipe.proteinG(),
                    recipe.fatG()))
        .toList();
  }
}

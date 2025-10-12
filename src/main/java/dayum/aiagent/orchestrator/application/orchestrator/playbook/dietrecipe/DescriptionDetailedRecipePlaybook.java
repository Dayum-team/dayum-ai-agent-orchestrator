package dayum.aiagent.orchestrator.application.orchestrator.playbook.dietrecipe;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import dayum.aiagent.orchestrator.client.chat.dto.GeneratedSuggestionsResponse;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DescriptionDetailedRecipePlaybook implements Playbook {

  private final ChatClientService chatClientService;
  private final ObjectMapper objectMapper;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.DESCRIBE_DETAILED_RECIPE.name())
          .action("사용자가 선택한 특정 레시피의 상세한 조리법과 정보를 설명한다.")
          .requiresContext(List.of(ContextType.RECOMMENDED_RECIPE.name()))
          .trigger(List.of("추천받은 레시피 중 하나를 선택하여 상세 정보를 요청", "1번 레시피 알려줘", "닭가슴살 샐러드 어떻게 만들어?"))
          .cautions(List.of("반드시 RECOMMENDED_RECIPE 컨텍스트가 대화에 포함된 경우에만 선택"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    RecommendedRecipeContext recipeContext =
        (RecommendedRecipeContext) context.contexts().get(ContextType.RECOMMENDED_RECIPE);
    PantryContext pantryContext = (PantryContext) context.contexts().get(ContextType.PANTRY);

    if (pantryContext == null || pantryContext.ingredients().isEmpty()) {
      throw new RuntimeException("Pantry context is missing or empty.");
    }
    if (recipeContext == null || recipeContext.recipes().isEmpty()) {
      return new PlaybookResult("상세 설명 실패", "먼저 레시피를 추천 받아주세요.", List.of(), Map.of());
    }

    RecommendedRecipeContext.RecommendedRecipe selectedRecipe =
        chatClientService.selectRecipeFromList(userMessage, recipeContext.recipes());
    if (selectedRecipe == null) {
      return new PlaybookResult(
          "레시피 선택 실패", "어떤 레시피를 말씀하시는지 잘 모르겠어요. 번호나 이름으로 다시 말씀해 주시겠어요?", List.of(), Map.of());
    }

    RecommendedRecipeContext.RecommendedRecipe finalRecipe = selectedRecipe;
    String finalDescription;

    try {
      if (selectedRecipe.source() == RecommendedRecipeContext.RecipeSource.LLM_GENERATE) {
        GeneratedSuggestionsResponse.Suggestion suggestion =
            new GeneratedSuggestionsResponse.Suggestion(
                selectedRecipe.title(), selectedRecipe.description(), null);

        String detailedDescriptionResponse =
            chatClientService.generateDetailedRecipe(
                context, pantryContext.ingredients(), suggestion);

        if (detailedDescriptionResponse != null && !detailedDescriptionResponse.isBlank()) {
          finalRecipe =
              new RecommendedRecipeContext.RecommendedRecipe(
                  selectedRecipe.title(),
                  detailedDescriptionResponse,
                  selectedRecipe.source(),
                  selectedRecipe.ingredients(),
                  selectedRecipe.calories(),
                  selectedRecipe.carbohydrates(),
                  selectedRecipe.proteins(),
                  selectedRecipe.fats());
        }
      }

      finalDescription = finalRecipe.description();

    } catch (Exception e) {
      log.error("레시피 상세 정보 처리 중 오류 발생", e);
      finalDescription = "{\"error\": \"레시피 정보를 처리하는 중 오류가 발생했어요.\"}";
    }

    return new PlaybookResult(
        "상세 레시피 데이터 조회 완료",
        finalDescription,
        Collections.emptyList(),
        Map.of(
            ContextType.RECOMMENDED_RECIPE,
            new RecommendedRecipeContext(List.of(finalRecipe)))
        );
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.DESCRIBE_DETAILED_RECIPE;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of(ContextType.RECOMMENDED_RECIPE);
  }
}

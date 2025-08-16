package dayum.aiagent.orchestrator.application.tools.dietrecipe;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.Tool;
import dayum.aiagent.orchestrator.application.tools.ToolType;
import dayum.aiagent.orchestrator.application.tools.dietrecipe.model.RecommendDietRecipeResponse;
import dayum.aiagent.orchestrator.application.tools.dietrecipe.model.RecommendDietRecipesRequest;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import dayum.aiagent.orchestrator.client.dayum.DayumApiClient;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendDietRecipesTool implements Tool<RecommendDietRecipesRequest> {

  private static final int RECOMMEND_MAX_COUNT = 5;
  private final DayumApiClient dayumApiClient;

  @Override
  public ToolType getType() {
    return ToolType.RECOMMEND_DIET_RECIPE;
  }

  @Override
  public ToolSignatureSchema.JsonSchema getSchema() {
    return ToolSignatureSchema.ObjectSchema.object()
        .property(
            "ingredients",
            ToolSignatureSchema.ArraySchema.array(
                    ToolSignatureSchema.ObjectSchema.object()
                        .property("name", ToolSignatureSchema.StringSchema.string().build())
                        .property("quantity", ToolSignatureSchema.StringSchema.string().build())
                        .required("name")
                        .build())
                .build())
        .build();
  }

  @Override
  public Class<RecommendDietRecipesRequest> getRequestType() {
    return RecommendDietRecipesRequest.class;
  }

  @Override
  public List<RecommendDietRecipeResponse> execute(
      ConversationContext context, RecommendDietRecipesRequest request) {
    return dayumApiClient.recommendContentsBy(request.ingredients(), RECOMMEND_MAX_COUNT).stream()
        .map(
            contents ->
                new RecommendDietRecipeResponse(
                    contents.thumbnailUrl(),
                    contents.url(),
                    contents.ingredients().stream()
                        .map(
                            ingredient ->
                                new Ingredient(
                                    ingredient.name(),
                                    ingredient.quantity() + " x " + ingredient.standardQuantity()))
                        .toList(),
                    contents.calories(),
                    contents.carbohydrates(),
                    contents.proteins(),
                    contents.fats()))
        .toList();
  }
}

package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.model.request.RecipeContentsRecommendRequest;
import dayum.aiagent.orchestrator.application.tools.model.response.RecipeContentsRecommendResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import dayum.aiagent.orchestrator.client.dayum.DayumApiClient;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeContentsRecommendTool implements Tool<RecipeContentsRecommendRequest> {

  private static final int RECOMMEND_MAX_COUNT = 5;
  private final DayumApiClient dayumApiClient;

  @Override
  public String getName() {
    return "recommend_diet_contents_with_ingredients_user_have";
  }

  @Override
  public String getDescription() {
    return "사용자가 가지고있는 재료를 가지고 Dayum 시스템을 이용해 다이어트 릴스 컨텐츠를 추천해주는 도구";
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
  public Class<RecipeContentsRecommendRequest> getRequestType() {
    return RecipeContentsRecommendRequest.class;
  }

  @Override
  public List<RecipeContentsRecommendResponse> execute(
      ConversationContext context, RecipeContentsRecommendRequest request) {
    return dayumApiClient.recommendContentsBy(request.ingredients(), RECOMMEND_MAX_COUNT).stream()
        .map(
            contents ->
                new RecipeContentsRecommendResponse(
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

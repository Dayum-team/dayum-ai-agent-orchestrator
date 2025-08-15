package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.model.request.RecipeGenerateRequest;
import dayum.aiagent.orchestrator.application.tools.model.response.RecipeContentsRecommendResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import dayum.aiagent.orchestrator.client.dayum.DayumApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeContentsRecommendTool implements Tool<RecipeGenerateRequest> {

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
            ToolSignatureSchema.ObjectSchema.object()
                .property("name", ToolSignatureSchema.StringSchema.string().build())
                .property("quantity", ToolSignatureSchema.StringSchema.string().build())
                .required("name")
                .build())
        .build();
  }

  @Override
  public Class<RecipeGenerateRequest> getRequestType() {
    return RecipeGenerateRequest.class;
  }

  @Override
  public RecipeContentsRecommendResponse execute(
      ConversationContext context, RecipeGenerateRequest request) {
    // TODO: Not Implemented
    return null;
  }
}

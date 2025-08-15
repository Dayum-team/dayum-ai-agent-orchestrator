package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.model.request.RecipeGenerateRequest;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeGenerateTool implements Tool<RecipeGenerateRequest> {

  private final ChatClientService chatClientService;

  @Override
  public String getName() {
    return "generate_diet_recipes_with_ingredients_user_have";
  }

  @Override
  public String getDescription() {
    return "사용자가 가지고있는 재료를 가지고 다이어트 레시피를 만들어주는 도구";
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
  public String execute(ConversationContext context, RecipeGenerateRequest request) {
    return chatClientService.generateDietRecipes(context, request.ingredients());
  }
}

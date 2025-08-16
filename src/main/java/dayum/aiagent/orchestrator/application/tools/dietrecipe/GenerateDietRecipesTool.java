package dayum.aiagent.orchestrator.application.tools.dietrecipe;

import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.Tool;
import dayum.aiagent.orchestrator.application.tools.ToolType;
import dayum.aiagent.orchestrator.application.tools.dietrecipe.model.GenerateDietRecipesRequest;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateDietRecipesTool implements Tool<GenerateDietRecipesRequest> {

  private final ChatClientService chatClientService;

  @Override
  public ToolType getType() {
    return ToolType.GENERATE_DIET_RECIPE;
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
  public Class<GenerateDietRecipesRequest> getRequestType() {
    return GenerateDietRecipesRequest.class;
  }

  @Override
  public String execute(ConversationContext context, GenerateDietRecipesRequest request) {
    return chatClientService.generateDietRecipes(context, request.ingredients());
  }
}

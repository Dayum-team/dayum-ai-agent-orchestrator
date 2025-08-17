package dayum.aiagent.orchestrator.client.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningPlaybookResponse;
import dayum.aiagent.orchestrator.client.chat.dto.Schema;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatClientService {

  private final Handlebars handlebars = new Handlebars();

  private final ChatClient chatClient;
  private final ObjectMapper objectMapper;

  public PlanningPlaybookResponse planningSteps(
      ConversationContext context,
      UserMessage userMessage,
      Map<PlaybookType, PlaybookCatalog> catalogs) {
    try {
      String userMessagePrompt =
          handlebars
              .compileInline(ChatPrompt.PlannerPrompt.USER_MESSAGE_TEMPLATE)
              .apply(
                  new HashMap<String, Object>() {
                    {
                      this.put("userMessage", userMessage.getMessage());
                      this.put(
                          "playbookList",
                          new Handlebars.SafeString(
                              objectMapper.writeValueAsString(catalogs.keySet())));
                      this.put(
                          "playbookCatalog",
                          new Handlebars.SafeString(objectMapper.writeValueAsString(catalogs)));
                    }
                  });
      ChatCompletionResponse response =
          chatClient.chatCompletionForStructuredMessage(
              ChatPrompt.PlannerPrompt.SYSTEM_MESSAGE,
              userMessagePrompt,
              context,
              Schema.ObjectSchema.object()
                  .property(
                      "steps",
                      Schema.ArraySchema.array(
                              Schema.ObjectSchema.object()
                                  .property("playbook_id", Schema.StringSchema.string().build())
                                  .property("reason", Schema.StringSchema.string().build())
                                  .property("priority", Schema.IntegerSchema.integer().build())
                                  .required("playbook_id", "reason", "priority")
                                  .build())
                          .build())
                  .build(),
              ModelType.HCX_007);
      return objectMapper.readValue(response.message(), PlanningPlaybookResponse.class);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new PlanningPlaybookResponse(new ArrayList<>());
    }
  }

  public String summary(
      String beforeRollingSummary, UserMessage userMessage, String receivedMessage) {
    try {
      String userMessagePrompt =
          handlebars
              .compileInline(ChatPrompt.RollingSummaryPrompt.USER_MESSAGE_TEMPLATE)
              .apply(
                  new HashMap<String, Object>() {
                    {
                      this.put("beforeRollingSummary", beforeRollingSummary);
                      this.put("userMessage", userMessage.getMessage());
                      this.put("receivedMessage", receivedMessage);
                    }
                  });
      ChatCompletionResponse response =
          chatClient.chatCompletion(
              ChatPrompt.RollingSummaryPrompt.SYSTEM_MESSAGE, userMessagePrompt, ModelType.HCX_005);
      if ("stop".equals(response.finishReason())) {
        return response.message();
      }
      throw new RuntimeException("Invalid finish reason.");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return beforeRollingSummary;
    }
  }

  public String generateDietRecipes(ConversationContext context, List<Ingredient> ingredients) {
    try {
      String userMessagePrompt =
          handlebars
              .compileInline(ChatPrompt.GenerateRecipesPrompt.USER_MESSAGE_TEMPLATE)
              .apply(
                  new HashMap<String, Object>() {
                    {
                      this.put(
                          "ingredientsJson",
                          new Handlebars.SafeString(objectMapper.writeValueAsString(ingredients)));
                      this.put("recipeCount", 3);
                    }
                  });
      ChatCompletionResponse response =
          chatClient.chatCompletion(
              ChatPrompt.GenerateRecipesPrompt.SYSTEM_MESSAGE,
              userMessagePrompt,
              context,
              ModelType.HCX_005);
      if ("stop".equals(response.finishReason())) {
        return response.message();
      }
      throw new RuntimeException("Invalid finish reason.");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return "";
    }
  }
}

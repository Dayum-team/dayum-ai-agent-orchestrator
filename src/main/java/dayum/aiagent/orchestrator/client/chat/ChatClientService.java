package dayum.aiagent.orchestrator.client.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ExtractIngredientsResponse;
import dayum.aiagent.orchestrator.client.chat.dto.GeneratedRecipesResponse;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningPlaybookResponse;
import dayum.aiagent.orchestrator.client.chat.schema.JsonSchemaGenerator;
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

  // TODO: 템플릿 형태로 유연하게 변경 필요
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
                      this.put(
                          "currentContextKey",
                          new Handlebars.SafeString(
                              objectMapper.writeValueAsString(context.contexts().keySet())));
                    }
                  });
      log.info("userMessagePrompt {}", userMessagePrompt);
      ChatCompletionResponse response =
          chatClient.chatCompletionWithStructuredOutput(
              ChatPrompt.PlannerPrompt.SYSTEM_MESSAGE,
              userMessagePrompt,
              JsonSchemaGenerator.generate(PlanningPlaybookResponse.class),
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

  public GeneratedRecipesResponse generateDietRecipes(
      ConversationContext context, List<Ingredient> ingredients) {
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
          chatClient.chatCompletionWithStructuredOutput(
              ChatPrompt.GenerateRecipesPrompt.SYSTEM_MESSAGE,
              userMessagePrompt,
              context,
              JsonSchemaGenerator.generate(GeneratedRecipesResponse.class),
              ModelType.HCX_007);
      return objectMapper.readValue(response.message(), GeneratedRecipesResponse.class);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Invalid finish reason.");
    }
  }

  public String generateResponseMessage(
      String reason, ConversationContext context, UserMessage userMessage, String systemMessage) {
    try {
      String userMessagePrompt =
          handlebars
              .compileInline(ChatPrompt.USER_MESSAGE_TEMPLATE)
              .apply(
                  new HashMap<String, Object>() {
                    {
                      this.put("reason", reason);
                      this.put("userMessage", userMessage.getMessage());
                    }
                  });
      ChatCompletionResponse response =
          chatClient.chatCompletion(systemMessage, userMessagePrompt, context, ModelType.HCX_005);
      return response.message();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Invalid finish reason.");
    }
  }

  public ExtractIngredientsResponse extractIngredientsFromText(String reason, String message) {
    try {
      String userMessagePrompt =
          handlebars
              .compileInline(ChatPrompt.ExtractIngredientPrompt.USER_MESSAGE_TEMPLATE_FOR_TEXT)
              .apply(
                  new HashMap<String, Object>() {
                    {
                      this.put("reason", reason);
                      this.put("userMessage", message);
                    }
                  });
      ChatCompletionResponse response =
          chatClient.chatCompletionWithStructuredOutput(
              ChatPrompt.ExtractIngredientPrompt.SYSTEM_MESSAGE_FOR_TEXT,
              userMessagePrompt,
              JsonSchemaGenerator.generate(ExtractIngredientsResponse.class),
              ModelType.HCX_007);
      log.info("✅✅✅✅✅ ExtractIngredientsResponse {}", response);
      return objectMapper.readValue(response.message(), ExtractIngredientsResponse.class);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Invalid finish reason.");
    }
  }

  public ExtractIngredientsResponse extractIngredientsFromImage(String reason, String imageUrl) {
    try {
      String userMessagePrompt =
          handlebars
              .compileInline(ChatPrompt.ExtractIngredientPrompt.USER_MESSAGE_TEMPLATE_FOR_IMAGE)
              .apply(
                  new HashMap<String, Object>() {
                    {
                      this.put("reason", reason);
                    }
                  });
      ChatCompletionResponse response =
          chatClient.chatCompletionWithImage(
              ChatPrompt.ExtractIngredientPrompt.SYSTEM_MESSAGE_FOR_IMAGE,
              userMessagePrompt,
              imageUrl,
              ModelType.HCX_005);
      return objectMapper.readValue(response.message(), ExtractIngredientsResponse.class);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Invalid finish reason.");
    }
  }
}

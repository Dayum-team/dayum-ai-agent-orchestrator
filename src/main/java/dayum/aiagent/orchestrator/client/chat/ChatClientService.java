package dayum.aiagent.orchestrator.client.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.dto.PlanningPlaybookResponse;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dayum.aiagent.orchestrator.common.vo.UserMessage;
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

  public PlanningPlaybookResponse planningPlaybooks(Map<PlaybookType, PlaybookCatalog> catalogs) {
    var responseFormat =
        ToolSignatureSchema.ObjectSchema.object()
            .property(
                "steps",
                ToolSignatureSchema.ArraySchema.array(
                        ToolSignatureSchema.ObjectSchema.object()
                            .property(
                                "playbook_id", ToolSignatureSchema.StringSchema.string().build())
                            .property(
                                "priority", ToolSignatureSchema.IntegerSchema.integer().build())
                            .required("playbook_id", "priority")
                            .build())
                    .build())
            .build();
    return new PlanningPlaybookResponse(new ArrayList<>());
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
              ChatPrompt.RollingSummaryPrompt.SYSTEM_MESSAGE, userMessagePrompt);
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
              ChatPrompt.GenerateRecipesPrompt.SYSTEM_MESSAGE, userMessagePrompt, context);
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

package dayum.aiagent.orchestrator.client.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.client.chat.dto.ChatCompletionResponse;
import java.util.HashMap;
import java.util.List;
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

  public String summary(String beforeRollingSummary, String userMessage, String response) {
    return "";
  }

  public String generateDietRecipes(ConversationContext context, List<Ingredient> ingredients) {
    try {
      String userMessage =
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
              ChatPrompt.GenerateRecipesPrompt.SYSTEM_MESSAGE, userMessage, context);
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

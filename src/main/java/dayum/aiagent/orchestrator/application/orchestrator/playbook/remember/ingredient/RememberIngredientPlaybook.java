package dayum.aiagent.orchestrator.application.orchestrator.playbook.remember.ingredient;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.client.chat.ChatClientService;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RememberIngredientPlaybook implements Playbook {

  private final ChatClientService chatClientService;
  private final RememberIngredientResponseBuilder responseBuilder;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.REMEMBER_INGREDIENT.name())
          .action(
              "사용자가 보낸 메시지/텍스트/이미지에 포함된 재료를 PANTRY context 에 반영하기 위해 활용. 절대 사용자로부터 재료를 파악하기위해 활용할 수 없음.")
          .outputContext(List.of(ContextType.PANTRY.name()))
          .trigger(List.of("음식/재료에 대한 메시지", "사용자가 가지고 있는 재료를 알려주는 경우", "음식/재료가 포함된 이미지를 보내는 경우"))
          .cautions(
              List.of(
                  "반드시 메시지에 재료에 대한 텍스트 혹은 이미지가 포함되어있는 경우",
                  "재료에 대한 정보를 저장하기 위해 사용한다.",
                  "(중요!!!) 절대 사용자로부터 재료를 파악하기 위해서 사용할 수 없다."))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(String reason, ConversationContext context, UserMessage userMessage) {
    List<Ingredient> extractedIngredients = new ArrayList<>();
    if (userMessage.imageUrl() != null && !userMessage.imageUrl().isEmpty()) {
      var response = chatClientService.extractIngredientsFromImage(reason, userMessage.imageUrl());
      extractedIngredients.addAll(response.ingredients());
    }
    if (!userMessage.message().isEmpty()) {
      var response = chatClientService.extractIngredientsFromText(reason, userMessage.getMessage());
      if (response != null && response.ingredients() != null) {
        extractedIngredients.addAll(response.ingredients());
      }
    }
    if (extractedIngredients.isEmpty()) {
      return responseBuilder.createNoIngredientsResponse();
    }

    return responseBuilder.createSuccessResponse(
        extractedIngredients, new PantryContext(extractedIngredients));
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.REMEMBER_INGREDIENT;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of();
  }
}

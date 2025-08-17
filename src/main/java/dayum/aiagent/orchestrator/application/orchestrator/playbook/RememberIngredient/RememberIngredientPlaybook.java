package dayum.aiagent.orchestrator.application.orchestrator.playbook.RememberIngredient;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.application.tools.ToolRegistry;
import dayum.aiagent.orchestrator.application.tools.ToolType;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RememberIngredientPlaybook implements Playbook {

  private final ToolRegistry toolRegistry;
  private final ObjectMapper objectMapper;
  private final RememberIngredientResponseBuilder responseBuilder;

  private static final PlaybookCatalog CATALOG =
      PlaybookCatalog.builder()
          .id(PlaybookType.REMEMBER_INGREDIENT.name())
          .action("사용자가 보낸 메시지/텍스트/이미지에 포함된 재료를 PANTRY context 에 반영하기 위해 활용")
          .outputContext(List.of(ContextType.PANTRY.name()))
          .trigger(List.of("음식/재료에 대한 메시지", "사용자가 가지고 있는 재료를 알려주는 경우", "음식/재료가 포함된 이미지를 보내는 경우"))
          .build();

  @Override
  public PlaybookCatalog getCatalog() {
    return CATALOG;
  }

  @Override
  public PlaybookResult play(ConversationContext context, UserMessage userMessage) {
    List<Ingredient> extractedIngredients;

    // 이미지 URL이 있는 경우
    if (userMessage.imageUrl() != null && !userMessage.imageUrl().isEmpty()) {
      extractedIngredients = extractIngredientsFromImage(context, userMessage.imageUrl());

      if (extractedIngredients.isEmpty()) {
        return responseBuilder.createNoIngredientsInImageResponse();
      }
    } else {
      extractedIngredients = extractIngredientsFromText(context, userMessage.getMessage());

      if (extractedIngredients.isEmpty()) {
        return responseBuilder.createNoIngredientsInTextResponse();
      }
    }
    PantryContext updatedPantry = mergePantry(context, extractedIngredients);

    return responseBuilder.createSuccessResponse(extractedIngredients, updatedPantry);
  }

  @Override
  public PlaybookType getType() {
    return PlaybookType.REMEMBER_INGREDIENT;
  }

  @Override
  public List<ContextType> getRequiresContext() {
    return List.of();
  }

  private List<Ingredient> extractIngredientsFromImage(
      ConversationContext context, String imageUrl) {
    try {
      String response =
          toolRegistry.execute(
              ToolType.EXTRACT_INGREDIENTS_FROM_IMAGE.getName(), imageUrl, context);

      return parseIngredients(response);
    } catch (Exception e) {
      log.error("Failed to extract ingredients from image", e);
      return List.of();
    }
  }

  private List<Ingredient> extractIngredientsFromText(ConversationContext context, String text) {
    try {
      String response =
          toolRegistry.execute(ToolType.EXTRACT_INGREDIENTS_FROM_TEXT.getName(), text, context);

      return parseIngredients(response);
    } catch (Exception e) {
      log.error("Failed to extract ingredients from text", e);
      return List.of();
    }
  }

  private List<Ingredient> parseIngredients(String response) throws Exception {
    if (response == null || response.trim().isEmpty() || response.equals("[]")) {
      return List.of();
    }

    List<Map<String, String>> rawIngredients =
        objectMapper.readValue(
            response, objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

    return rawIngredients.stream()
        .map(map -> new Ingredient(map.get("name")))
        .filter(ing -> ing.name() != null && !ing.name().isEmpty())
        .toList();
  }

  private PantryContext mergePantry(ConversationContext context, List<Ingredient> newIngredients) {
    ContextValue existingPantry = context.contexts().get(ContextType.PANTRY);
    List<Ingredient> allIngredients = new ArrayList<>(newIngredients);

    if (existingPantry instanceof PantryContext pantryContext) {
      Set<String> newIngredientNames =
          newIngredients.stream().map(Ingredient::name).collect(Collectors.toSet());

      pantryContext.ingredients().stream()
          .filter(ing -> !newIngredientNames.contains(ing.name()))
          .forEach(allIngredients::add);
    }

    return new PantryContext(allIngredients);
  }
}

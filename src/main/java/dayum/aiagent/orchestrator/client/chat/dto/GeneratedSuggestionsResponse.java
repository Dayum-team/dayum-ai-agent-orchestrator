package dayum.aiagent.orchestrator.client.chat.dto;

import java.util.List;

public record GeneratedSuggestionsResponse(List<Suggestion> suggestions) {
  public record Suggestion(
      String title, String description, List<IngredientUsed> ingredientsUsed) {}

  public record IngredientUsed(String name) {}
}

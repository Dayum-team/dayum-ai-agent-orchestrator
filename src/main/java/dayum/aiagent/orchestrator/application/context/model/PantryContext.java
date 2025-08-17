package dayum.aiagent.orchestrator.application.context.model;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record PantryContext(List<Ingredient> ingredients) implements ContextValue {

  @Override
  public ContextValue merge(ContextValue newValue) {
    if (newValue instanceof PantryContext) {
      PantryContext pantryContext = (PantryContext) newValue;
      List<Ingredient> mergedIngredients =
          Stream.concat(
                  Optional.ofNullable(this.ingredients).orElseGet(List::of).stream(),
                  Optional.ofNullable(pantryContext.ingredients()).orElseGet(List::of).stream())
              .collect(
                  Collectors.collectingAndThen(
                      Collectors.toMap(
                          ingredient -> ingredient.name().trim(),
                          ingredient ->
                              Optional.ofNullable(ingredient.quantity()).orElse("").trim(),
                          (x, y) -> x.isEmpty() ? y : y.isEmpty() ? x : x + " + " + y,
                          LinkedHashMap::new),
                      m ->
                          m.entrySet().stream()
                              .map(e -> new Ingredient(e.getKey(), e.getValue()))
                              .toList()));
      return new PantryContext(mergedIngredients);
    }
    return this;
  }
}

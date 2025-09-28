package dayum.aiagent.orchestrator.client.dayum.dto;

import dayum.aiagent.orchestrator.common.vo.Ingredient;

public record IngredientResponse(
    String name,
    String standardQuantity,
    long quantity,
    Double calories,
    Double carbohydrates,
    Double proteins,
    Double fats) {

	public Ingredient toIngredient() {
		return new Ingredient(name, String.valueOf(quantity));
	}
}

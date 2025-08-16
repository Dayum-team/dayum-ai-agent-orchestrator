package dayum.aiagent.orchestrator.client.dayum.dto;

public record IngredientResponse(
    String name,
    String standardQuantity,
    long quantity,
    Double calories,
    Double carbohydrates,
    Double proteins,
    Double fats) {}

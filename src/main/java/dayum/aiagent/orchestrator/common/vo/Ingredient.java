package dayum.aiagent.orchestrator.common.vo;

public record Ingredient(String name, String quantity) {

  public Ingredient(String name) {
    this(name, "");
  }
}

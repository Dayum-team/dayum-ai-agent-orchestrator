package dayum.aiagent.orchestrator.application.tools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ToolType {
  RECOMMEND_DIET_RECIPE(
      "recommend_diet_contents_with_ingredients_user_have",
      "사용자가 가지고있는 재료를 가지고 Dayum 시스템을 이용해 다이어트 릴스 컨텐츠를 추천해주는 도구"),
  GENERATE_DIET_RECIPE(
      "generate_diet_recipes_with_ingredients_user_have", "사용자가 가지고있는 재료를 가지고 다이어트 레시피를 만들어주는 도구"),

  /** 임시 * */
  EXTRACT_INGREDIENTS_FROM_IMAGE("", ""),

  EXTRACT_INGREDIENTS_FROM_TEXT("", ""),

  EXTRACT_TASTE_ATTRIBUTE("", ""),

  GENERATE_SELECTION_ACK("", ""),

  GENERATE_SMALL_TALK("", "");

  private final String name;

  private final String description;
}

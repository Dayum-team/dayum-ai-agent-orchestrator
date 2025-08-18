package dayum.aiagent.orchestrator.application.tools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ToolType {
  /** 임시 * */
  EXTRACT_INGREDIENTS_FROM_IMAGE("", ""),

  EXTRACT_INGREDIENTS_FROM_TEXT("", ""),

  EXTRACT_TASTE_ATTRIBUTE("", ""),

  GENERATE_SELECTION_ACK("", ""),

  GENERATE_SMALL_TALK("", "");

  private final String name;
  private final String description;
}

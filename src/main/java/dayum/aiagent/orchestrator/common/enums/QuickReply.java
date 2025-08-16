package dayum.aiagent.orchestrator.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuickReply {
  PICK_THIS("마음에 들어!"),
  FIND_ALTERNATIVES("다른 레시피를 찾아줘"),
  GENERATE_FROM_INGREDIENTS("직접 만들어줘");

  private final String displayMessage;
}

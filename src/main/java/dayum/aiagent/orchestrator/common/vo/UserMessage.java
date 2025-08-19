package dayum.aiagent.orchestrator.common.vo;

import dayum.aiagent.orchestrator.common.enums.QuickReply;
import jakarta.annotation.Nullable;

public record UserMessage(String message, @Nullable QuickReply selectQuickReply, String imageUrl) {

  public String getMessage() {
    if (selectQuickReply == null) {
      return message;
    }
    return message + "\n" + selectQuickReply.getDisplayMessage();
  }
}

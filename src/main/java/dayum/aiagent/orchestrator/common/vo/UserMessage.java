package dayum.aiagent.orchestrator.common.vo;

import dayum.aiagent.orchestrator.common.enums.QuickReply;
import jakarta.annotation.Nullable;

public record UserMessage(String message, @Nullable QuickReply selectQuickRely, String imageUrl) {

  public String getMessage() {
    if (selectQuickRely == null) {
      return message;
    }
    return message + "\n" + selectQuickRely.getDisplayMessage();
  }
}

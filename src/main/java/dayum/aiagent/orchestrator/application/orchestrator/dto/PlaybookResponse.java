package dayum.aiagent.orchestrator.application.orchestrator.dto;

import dayum.aiagent.orchestrator.common.enums.QuickReply;
import java.util.List;

public record PlaybookResponse(String title, String message, List<QuickReply> quickReplies) {}

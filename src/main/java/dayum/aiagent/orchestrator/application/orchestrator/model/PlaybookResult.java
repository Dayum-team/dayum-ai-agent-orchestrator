package dayum.aiagent.orchestrator.application.orchestrator.model;

import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ContextValue;
import dayum.aiagent.orchestrator.common.enums.QuickReply;
import java.util.List;
import java.util.Map;

public record PlaybookResult(
    String title,
    String message,
    List<QuickReply> quickReplies,
    Map<ContextType, ContextValue> output) {}

package dayum.aiagent.orchestrator.application.context.model;

import java.util.List;
import java.util.Map;

public record ConversationContext(
    long memberId,
    String sessionId,
    Map<ContextType, ContextValue> contexts,
    List<ShortTermContext> shortTermContexts,
    String rollingSummary) {}

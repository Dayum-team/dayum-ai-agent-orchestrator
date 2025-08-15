package dayum.aiagent.orchestrator.application.context.dto;

import java.util.List;

public record ConversationContext(
    long memberId,
    String sessionId,
    List<ShortTermContext> shortTermContexts,
    String rollingSummary) {}

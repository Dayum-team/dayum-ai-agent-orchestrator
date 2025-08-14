package dayum.aiagent.orchestrator.application.context.dto;

import java.util.List;

public record ConversationContext(
    String sessionId,
    String memberId,
    List<ShortTermContext> shortTermContexts,
    String rollingSummary) {}

package dayum.aiagent.orchestrator.common.vo;

import dayum.aiagent.orchestrator.common.enums.QuickReply;
import java.util.List;

public record AgentMessage(String message, List<QuickReply> quickReplies) {}

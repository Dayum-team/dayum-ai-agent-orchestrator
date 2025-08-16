package dayum.aiagent.orchestrator.application.context.model;

import dayum.aiagent.orchestrator.common.vo.UserMessage;

public record ShortTermContext(UserMessage userMessage, String receivedMessage) {}

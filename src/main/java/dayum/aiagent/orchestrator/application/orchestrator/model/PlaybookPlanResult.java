package dayum.aiagent.orchestrator.application.orchestrator.model;


import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;

public record PlaybookPlanResult(PlaybookType type, Playbook playbook, String reason) {}

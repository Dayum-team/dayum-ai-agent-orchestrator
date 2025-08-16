package dayum.aiagent.orchestrator.application.orchestrator.playbook.model;

import java.util.List;

public record PlaybookCatalog(
    String id,
    String action,
    List<String> trigger,
    List<String> requiresContext,
    List<String> cautions) {}

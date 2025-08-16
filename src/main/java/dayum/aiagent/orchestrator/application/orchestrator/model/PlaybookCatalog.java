package dayum.aiagent.orchestrator.application.orchestrator.model;

import java.util.List;

import lombok.Builder;

@Builder
public record PlaybookCatalog(
    String id,
    String action,
    List<String> trigger,
    List<String> requiresContext,
	List<String> outputContext,
    List<String> cautions) {}

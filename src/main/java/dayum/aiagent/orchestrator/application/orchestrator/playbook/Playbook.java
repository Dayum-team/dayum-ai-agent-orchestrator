package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.orchestrator.dto.PlaybookResponse;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.model.PlaybookCatalog;

public interface Playbook {

  PlaybookCatalog getCatalog();

  PlaybookResponse play();
}

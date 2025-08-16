package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResponse;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelectionAckPlaybook implements Playbook {

  @Override
  public PlaybookCatalog getCatalog() {
    return null;
  }

  @Override
  public PlaybookResponse play() {
    return null;
  }
}

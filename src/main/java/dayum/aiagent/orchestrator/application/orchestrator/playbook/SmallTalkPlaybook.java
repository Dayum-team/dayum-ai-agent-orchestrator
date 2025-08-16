package dayum.aiagent.orchestrator.application.orchestrator.playbook;

import dayum.aiagent.orchestrator.application.orchestrator.dto.PlaybookResponse;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.model.PlaybookCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmallTalkPlaybook implements Playbook {

  @Override
  public PlaybookCatalog getCatalog() {
    return null;
  }

  @Override
  public PlaybookResponse play() {
    return null;
  }
}

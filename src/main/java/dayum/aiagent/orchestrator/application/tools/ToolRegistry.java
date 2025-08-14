package dayum.aiagent.orchestrator.application.tools;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToolRegistry {

  private final List<Tool<?, ?>> tools;

  public String getToolsSchema() {
    return "";
  }
}

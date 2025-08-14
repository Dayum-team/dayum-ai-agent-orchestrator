package dayum.aiagent.orchestrator.application.tools;

import dayum.aiagent.orchestrator.client.dayum.DayumApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendRecipeTool implements Tool<Object, Object> {

  private final DayumApiClient dayumApiClient;

  @Override
  public String getName() {
    return "";
  }
}

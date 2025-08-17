package dayum.aiagent.orchestrator.client.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelType {
  HCX_005("HCX-005"),
  HCX_007("HCX-007");

  private final String name;
}

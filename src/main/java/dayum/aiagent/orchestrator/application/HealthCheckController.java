package dayum.aiagent.orchestrator.application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping("/ping")
  public String health() {
    return "pong";
  }
}

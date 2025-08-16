package dayum.aiagent.orchestrator.client.dayum;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("dayum-server")
public class DayumServerProperties {

  private String baseUrl;
  private String apiKey;
}

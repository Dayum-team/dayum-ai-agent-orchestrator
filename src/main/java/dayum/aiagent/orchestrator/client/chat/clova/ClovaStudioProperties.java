package dayum.aiagent.orchestrator.client.chat.clova;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ncp.clova")
public class ClovaStudioProperties {

  private String baseUrl;
  private String apiKey;
}

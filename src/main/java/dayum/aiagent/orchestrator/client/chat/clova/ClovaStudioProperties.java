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

  private String url;
  private String apiKey;

  public static class ModelConfig {
    public static final double TOP_P = 0.8;
    public static final int TOP_K = 0;
    public static final int MAX_TOKENS = 1000;
    public static final double TEMPERATURE = 0.5;
    public static final double REPETITION_PENALTY = 1.1;
    public static final long SEED = 0;
    public static final boolean INCLUDE_AI_FILTERS = false;
  }
}

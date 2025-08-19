package dayum.aiagent.orchestrator.client.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "ncp")
public class NcpProperties {
  private String region;
  private String s3Endpoint;
  private String s3Bucket;
  private String accessKey;
  private String secretKey;
}

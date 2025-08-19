package dayum.aiagent.orchestrator.client.s3;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3ClientConfig {

  @Bean
  public S3Client ncpS3Client(NcpProperties ncp) {
    return S3Client.builder()
        .endpointOverride(URI.create(ncp.getS3Endpoint())) // ex) https://kr.object.ncloudstorage.com
        .region(Region.of(ncp.getRegion()))                // ex) ap-northeast-2
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(ncp.getAccessKey(), ncp.getSecretKey())
            )
        )
        .serviceConfiguration(
            S3Configuration.builder()
                .pathStyleAccessEnabled(true)      // NCP는 path-style 권장
                .checksumValidationEnabled(false)  // 네트워크 환경 따라 끔
                .build()
        )
        .build();
  }
}

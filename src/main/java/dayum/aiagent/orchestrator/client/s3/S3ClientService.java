package dayum.aiagent.orchestrator.client.s3;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ClientService {

  private final S3Client s3Client;
  private final NcpProperties ncp; // ncp.s3Endpoint / ncp.s3Bucket 등

  public String uploadBytes(String prefix, String filename, byte[] data) {
    return uploadBytes(prefix, filename, data, null);
  }

  public String uploadBytes(String prefix, String filename, byte[] data, @Nullable String contentType) {
    String bucket = ncp.getS3Bucket();

    // prefix 정리
    String cleanPrefix = (prefix == null) ? "" : prefix.replaceAll("^/+", "").replaceAll("/+$", "");

    // 파일명 정제 + 충돌 방지 키 생성
    String safeName = (filename == null || filename.isBlank())
        ? "file"
        : filename.replaceAll("[^a-zA-Z0-9._-]", "_");

    String ext = "";
    int dot = safeName.lastIndexOf('.');
    if (dot >= 0) ext = safeName.substring(dot);

    String key = (cleanPrefix.isEmpty() ? "" : cleanPrefix + "/") + UUID.randomUUID() + ext;

    PutObjectRequest.Builder put = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .acl(ObjectCannedACL.PUBLIC_READ); // 버킷이 public-read가 아닐 경우 제거

    if (contentType != null && !contentType.isBlank()) {
      put.contentType(contentType);
    }

    s3Client.putObject(put.build(), RequestBody.fromBytes(data));

    // 퍼블릭 URL (NCP 엔드포인트 기반)
    return ncp.getS3Endpoint().replaceAll("/+$", "") + "/" + bucket + "/" + key;
  }
}

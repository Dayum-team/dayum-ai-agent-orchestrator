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
  private final NcpProperties ncp;

  public String uploadBytes(String prefix, String filename, byte[] data) {
    return uploadBytes(prefix, filename, data, null);
  }

  public String uploadBytes(String prefix, String filename, byte[] data, @Nullable String contentType) {
    String bucket = ncp.getS3Bucket();

    String cleanPrefix = (prefix == null) ? "" : prefix.replaceAll("^/+", "").replaceAll("/+$", "");

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
        .acl(ObjectCannedACL.PUBLIC_READ);

    if (contentType != null && !contentType.isBlank()) {
      put.contentType(contentType);
    }

    s3Client.putObject(put.build(), RequestBody.fromBytes(data));

    return ncp.getS3Endpoint().replaceAll("/+$", "") + "/" + bucket + "/" + key;
  }
}

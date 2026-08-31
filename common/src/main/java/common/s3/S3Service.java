package common.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public String createPresignedUrl(
            String bucketName,
            String keyName,
            Map<String, String> metadata
    ) {
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(keyName);

        if (metadata != null && !metadata.isEmpty()) {
            putObjectRequestBuilder.metadata(metadata);
        }

        PutObjectRequest putObjectRequest = putObjectRequestBuilder.build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest
                .builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner
                .presignPutObject(presignRequest);

        return presignedRequest.url().toExternalForm();
    }

    public Optional<HeadObjectResponse> getFileMetaData(String bucketName, String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return Optional.of(s3Client.headObject(headObjectRequest));
        } catch(NoSuchKeyException e) {
            return Optional.empty();
        }
    }

    public void downloadFile(
            String fileKey,
            String bucketName,
            Path destinationPath
    ) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.getObject(getObjectRequest, destinationPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3: bucket=" + bucketName + ", key=" + fileKey, e);
        }
    }

    public void uploadLocalFile(
            String bucketName,
            String key,
            Path filePath,
            String contentType
    ) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3: bucket=" + bucketName + ", key=" + key, e);
        }
    }

    public void uploadFile(
            String preSignedUrlString,
            InputStream inputStream,
            long contentLength,
            Map<String, String> metadata
    ) {
        try {
            URL presignedUrl = new URI(preSignedUrlString).toURL();

            System.out.println("PresignedUrl for PUT upload " + presignedUrl);
            SdkHttpRequest.Builder requestBuilder = SdkHttpRequest
                    .builder()
                    .method(SdkHttpMethod.PUT)
                    .uri(presignedUrl.toURI());

            metadata.forEach((k, v) -> requestBuilder.putHeader("x-amz-meta-" + k, v));

            SdkHttpRequest request = requestBuilder.build();

            HttpExecuteRequest executeRequest = HttpExecuteRequest
                    .builder()
                    .request(request)
                    .contentStreamProvider(RequestBody.fromInputStream(inputStream, contentLength).contentStreamProvider())
                    .build();

            try (SdkHttpClient sdkHttpClient = ApacheHttpClient.create()) {
                System.out.println("Sending PUT request to S3");
                sdkHttpClient.prepareRequest(executeRequest).call();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

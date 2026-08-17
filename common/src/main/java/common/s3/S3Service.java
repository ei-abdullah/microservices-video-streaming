package common.s3;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Service
public class S3Service {

    public String createPresignedUrl(
            String bucketName,
            String keyName,
            Map<String, String> metadata
    ) {
        try (S3Presigner presigner = S3Presigner.create()) {
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest
                    .builder()
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(Duration.ofMinutes(10))
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner
                    .presignPutObject(presignRequest);

            return presignedRequest.url().toExternalForm();
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

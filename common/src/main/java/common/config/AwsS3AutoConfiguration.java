package common.config;

import common.s3.S3Bucket;
import common.s3.S3Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;


@Configuration
@ConditionalOnClass(S3Client.class)
@ConditionalOnProperty(prefix = "aws.s3", name = "bucket-name")
public class AwsS3AutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public S3Service s3Service(S3Client s3Client) {
        return new S3Service(s3Client);
    }

    @Bean
    @ConditionalOnMissingBean
    public S3Bucket s3Bucket() {
        return new S3Bucket();
    }

    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client() {
        return S3Client.create();
    }
}

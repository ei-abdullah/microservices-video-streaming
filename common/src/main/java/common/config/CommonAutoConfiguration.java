package common.config;

import common.htmlPage.HtmlPageService;
import common.jwt.JwtFilter;
import common.jwt.JwtService;
import common.s3.S3Bucket;
import common.s3.S3Service;
import common.userDetails.RemoteUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.s3.S3Client;



@Configuration
public class CommonAutoConfiguration {
    @Bean
    public S3Service s3Service(S3Client s3Client) {
        return new S3Service(s3Client);
    }

    @Bean
    @ConditionalOnProperty(prefix = "aws.s3", name = "bucket-name")
    public S3Bucket s3Bucket() {
        return new S3Bucket();
    }

    @Bean
    @ConditionalOnProperty(prefix = "aws.s3", name = "bucket-name")
    public S3Bucket s3Bucket() {
        return new S3Bucket();
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public HtmlPageService htmlPageService() {
        return new HtmlPageService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "security.jwt", name = "filter-enabled", havingValue = "true")
    public JwtFilter jwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        return new JwtFilter(jwtService, userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService remoteUserDetailsService(RestTemplate restTemplate) {
        return new RemoteUserDetailsService(restTemplate);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.create();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

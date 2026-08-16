package common.config;

import common.htmlPage.HtmlPageService;
import common.jwt.JwtFilter;
import common.jwt.JwtService;
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


@Configuration
public class CommonAutoConfiguration {
    @Bean
    public S3Service s3Service() {
        return new S3Service();
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

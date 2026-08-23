package common.config;

import common.jwt.JwtFilter;
import common.jwt.JwtService;
import common.userDetails.RemoteUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;


@Configuration
@ConditionalOnProperty(prefix = "security.jwt", name = "secret-key")
public class JwtSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService remoteUserDetailsService(RestTemplate restTemplate) {
        return new RemoteUserDetailsService(restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "security.jwt", name = "filter-enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public JwtFilter jwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        return new JwtFilter(jwtService, userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

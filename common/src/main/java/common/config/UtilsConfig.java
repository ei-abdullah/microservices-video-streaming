package common.config;

import common.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
    @Bean
    public Utils utils() {
        return new Utils();
    }
}

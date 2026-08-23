package common.config;

import common.htmlPage.HtmlPageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "app.html-page", name = "enabled", havingValue = "true")
public class HtmlPageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HtmlPageService htmlPageService() {
        return new HtmlPageService();
    }
}

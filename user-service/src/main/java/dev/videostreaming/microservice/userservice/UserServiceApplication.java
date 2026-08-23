package dev.videostreaming.microservice.userservice;

import common.config.CommonWebAutoConfiguration;
import common.config.HtmlPageAutoConfiguration;
import common.config.JwtSecurityAutoConfiguration;
import common.config.RabbitMqConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CommonWebAutoConfiguration.class, JwtSecurityAutoConfiguration.class, HtmlPageAutoConfiguration.class, RabbitMqConfig.class})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}

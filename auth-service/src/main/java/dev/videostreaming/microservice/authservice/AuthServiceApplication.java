package dev.videostreaming.microservice.authservice;

import common.config.CommonWebAutoConfiguration;
import common.config.JwtSecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CommonWebAutoConfiguration.class, JwtSecurityAutoConfiguration.class})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}

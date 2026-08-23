package dev.videostreaming.microservice.notificationservice;

import common.config.HtmlPageAutoConfiguration;
import common.config.RabbitMqConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RabbitMqConfig.class, HtmlPageAutoConfiguration.class})
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}

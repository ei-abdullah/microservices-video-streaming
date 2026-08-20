package dev.videostreaming.microservice.transcodingservice;

import common.config.RabbitMqConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RabbitMqConfig.class})
public class TranscodingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranscodingServiceApplication.class, args);
    }

}

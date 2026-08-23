package dev.videostreaming.microservice.transcodingservice;

import common.config.AwsS3AutoConfiguration;
import common.config.RabbitMqConfig;
import common.config.UtilsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AwsS3AutoConfiguration.class, RabbitMqConfig.class, UtilsConfig.class})
public class TranscodingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranscodingServiceApplication.class, args);
    }

}

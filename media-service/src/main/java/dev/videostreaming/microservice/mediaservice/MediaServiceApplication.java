package dev.videostreaming.microservice.mediaservice;

import common.config.AwsS3AutoConfiguration;
import common.config.CommonWebAutoConfiguration;
import common.config.JwtSecurityAutoConfiguration;
import common.config.UtilsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@EnableConfigurationProperties
@Import({CommonWebAutoConfiguration.class, AwsS3AutoConfiguration.class, JwtSecurityAutoConfiguration.class, UtilsConfig.class})
public class MediaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaServiceApplication.class, args);
	}

}

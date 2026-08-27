package dev.videostreaming.microservice.mediaservice.config;

import common.constant.MediaConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue queue() {
        return new Queue(MediaConstant.QUEUE, true);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(MediaConstant.EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MediaConstant.ROUTING_KEY)
                .noargs();
    }
}

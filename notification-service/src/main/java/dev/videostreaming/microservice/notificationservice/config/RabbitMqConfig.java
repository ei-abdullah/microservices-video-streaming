package dev.videostreaming.microservice.notificationservice.config;

import common.constant.NotificationConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue queue() {
        return new Queue(NotificationConstant.QUEUE, true);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(NotificationConstant.EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(NotificationConstant.ROUTING_KEY)
                .noargs();
    }
}
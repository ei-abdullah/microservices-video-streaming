package dev.videostreaming.microservice.transcodingservice.config;

import common.constant.TranscodingConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue queue() {
        return new Queue(TranscodingConstant.QUEUE, true);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(TranscodingConstant.EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(TranscodingConstant.ROUTING_KEY)
                .noargs();

    }
}

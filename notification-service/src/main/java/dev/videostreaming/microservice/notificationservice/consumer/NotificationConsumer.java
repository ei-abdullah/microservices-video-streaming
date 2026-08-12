package dev.videostreaming.microservice.notificationservice.consumer;


import common.constant.NotificationConstant;
import common.dto.NotificationEvent;
import dev.videostreaming.microservice.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = NotificationConstant.QUEUE)
    public void handle(NotificationEvent event) {
        switch (event.eventName()) {
            case "SIGNUP_VERIFICATION" ->
                    emailService.sendVerificationEmail(event.email(), event.verificationUri());

            default -> System.out.println("Unknown event: " + event.eventName());
        }
    }
}
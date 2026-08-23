package dev.videostreaming.microservice.transcodingservice.consumer;

import common.constant.TranscodingConstant;
import common.dto.TranscodingEvent;
import dev.videostreaming.microservice.transcodingservice.service.TranscodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TranscodingConsumer {

    private final TranscodingService transcodingService;

    @RabbitListener(queues = TranscodingConstant.QUEUE)
    public void handle(TranscodingEvent event) {
        Map<String, Object> vars = event.vars();

        switch (event.eventName()) {
            case TranscodingConstant.EVENT_ENQUEUE_MEDIA:
                String mediaId = (String) vars.get("mediaId");
                String mediaKey = (String) vars.get("mediaKey");
                String bucketName = (String) vars.get("bucketName");
                transcodingService.processMedia(mediaId, mediaKey, bucketName);
                break;

            default:
                System.out.println("Unknown event: " + event.eventName());
        }
    }
}

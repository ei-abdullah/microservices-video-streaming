package dev.videostreaming.microservice.mediaservice.consumer;

import common.constant.MediaConstant;
import common.dto.MediaEvent;
import common.dto.MediaMetadata;
import common.exception.ServerException;
import dev.videostreaming.microservice.mediaservice.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MediaConsumer {

    private final MediaService mediaService;

    @RabbitListener(queues = MediaConstant.QUEUE)
    public void handle(MediaEvent event) {

        switch (event.eventName()) {
            case MediaConstant.EVENT_READY_MEDIA, MediaConstant.EVENT_FAILED_MEDIA:
                String mediaId = event.mediaEventMap().mediaId();
                String masterPlaylistKey = event.mediaEventMap().masterPlaylistKey();
                String bucketName = event.mediaEventMap().bucketName();
                String status = event.mediaEventMap().status();
                String failureReason = event.mediaEventMap().failureReason();
                MediaMetadata metadata = event.mediaEventMap().metadata();

                mediaService.updateMediaStatus(
                        mediaId,
                        masterPlaylistKey,
                        bucketName,
                        status,
                        failureReason,
                        metadata
                );

                break;
            default:
                throw new ServerException("Unknown event: " + event.eventName());
        }
    }
}

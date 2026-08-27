package dev.videostreaming.microservice.mediaservice.service;

import common.Utils;
import common.constant.MediaConstant;
import common.constant.TranscodingConstant;
import common.dto.MediaMetadata;
import common.dto.TranscodingEvent;
import common.exception.NotFoundException;
import common.s3.S3Bucket;
import common.s3.S3Service;
import common.userDetails.RemoteUserPrincipal;
import dev.videostreaming.microservice.mediaservice.Media;
import dev.videostreaming.microservice.mediaservice.MediaStatus;
import dev.videostreaming.microservice.mediaservice.dto.response.CompleteMediaUploadResponse;
import dev.videostreaming.microservice.mediaservice.dto.response.CreateUploadResponse;
import dev.videostreaming.microservice.mediaservice.repository.MediaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MediaService {

    private final RabbitTemplate rabbitTemplate;
    private final MediaRepository mediaRepository;
    private final S3Service s3Service;
    private final S3Bucket s3Bucket;
    private final Utils utils;


    @Transactional
    public CreateUploadResponse createUpload(RemoteUserPrincipal user) {
        Media media = Media
                .builder()
                .uploaderId(user.getId())
                .sourceBucketName(s3Bucket.getBucketName())
                .status(MediaStatus.UPLOADING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        media = mediaRepository.save(media);

        String uploadMediaKey = utils.getRawMediaKey(
                user.getId(),
                media.getId()
        );

        media.setSourceObjectKey(uploadMediaKey);
        mediaRepository.save(media);

        String presignedUrl = s3Service.createPresignedUrl(
                s3Bucket.getBucketName(),
                uploadMediaKey,
                null
        );

        return new CreateUploadResponse(
                media.getId(),
                presignedUrl,
                String.valueOf(media.getStatus()),
                media.getCreatedAt(),
                media.getUpdatedAt()
        );
    }

    @Transactional
    public CompleteMediaUploadResponse completeMediaUpload(String mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NotFoundException("Media file with id" + mediaId + " not found!"));

        Optional<HeadObjectResponse> objectResponse = s3Service.getFileMetaData(
                s3Bucket.getBucketName(),
                media.getSourceObjectKey()
        );

        if (objectResponse.isEmpty()) {
            media.setFailureReason(MediaConstant.S3_ERROR);
            media.setStatus(MediaStatus.FAILED);
            media.setUpdatedAt(Instant.now());
            mediaRepository.save(media);

            return new CompleteMediaUploadResponse(
                    media.getId(),
                    "",
                    MediaConstant.S3_ERROR,
                    MediaStatus.FAILED.toString(),
                    media.getCreatedAt(),
                    media.getUpdatedAt()
            );
        }

        HeadObjectResponse metadata = objectResponse.get();

        media.setStatus(MediaStatus.UPLOADED);
        mediaRepository.save(media);
        media.setContentType(metadata.contentType());
        media.setFileSize(BigDecimal.valueOf(metadata.contentLength()));

        mediaRepository.save(media);

        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put("mediaId", media.getId());
        eventData.put("mediaKey", media.getSourceObjectKey());
        eventData.put("bucketName", media.getSourceBucketName());

        try {
            media.setStatus(MediaStatus.QUEUED);
            media.setUpdatedAt(Instant.now());
            mediaRepository.save(media);

            rabbitTemplate.convertAndSend(
                    TranscodingConstant.EXCHANGE,
                    TranscodingConstant.ROUTING_KEY_UPLOAD_MEDIA,
                    new TranscodingEvent(
                            TranscodingConstant.EVENT_ENQUEUE_MEDIA,
                            eventData
                    )
            );
        } catch (Exception e) {
            media.setStatus(MediaStatus.FAILED);
            media.setFailureReason(MediaConstant.QUEUE_FAILED);
            media.setUpdatedAt(Instant.now());
            mediaRepository.save(media);

            return new CompleteMediaUploadResponse(
                    media.getId(),
                    media.getTitle(),
                    media.getFailureReason(),
                    MediaStatus.FAILED.name(),
                    media.getCreatedAt(),
                    media.getUpdatedAt()
            );
        }

        return new CompleteMediaUploadResponse(
                media.getId(),
                media.getTitle(),
                "",
                MediaStatus.QUEUED.name(),
                media.getCreatedAt(),
                media.getUpdatedAt()
        );
    }

    public void updateMediaStatus(
            String mediaId,
            String masterPlaylistKey,
            String bucketName,
            String status,
            String failureReason,
            MediaMetadata metadata
    ) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NotFoundException("Media file with id" + mediaId + " not found!"));

        if (status.equals(MediaConstant.MEDIA_FAILED)) {
            media.setSourceBucketName(bucketName);
            media.setStatus(MediaStatus.FAILED);
            media.setFailureReason(failureReason);
            media.setUpdatedAt(Instant.now());
            mediaRepository.save(media);
            return;
        }

        media.setStatus(MediaStatus.READY);
        media.setMasterPlaylistKey(masterPlaylistKey);
        media.setDuration(metadata.duration());
        media.setWidth(metadata.width());
        media.setHeight(metadata.height());
        media.setUpdatedAt(Instant.now());
        mediaRepository.save(media);
    }
}

package dev.videostreaming.microservice.mediaservice.service;

import common.s3.S3Service;
import dev.videostreaming.microservice.mediaservice.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    public String getUploadUrl() {
        return s3Service.createPresignedUrl(
                "video-streaming-bucket",
                "video-streaming-key",
                null
        );
    }
}

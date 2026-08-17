package dev.videostreaming.microservice.mediaservice.service;

import common.Utils;
import common.s3.S3Bucket;
import common.s3.S3Service;
import common.userDetails.RemoteUserPrincipal;
import dev.videostreaming.microservice.mediaservice.dto.response.CreateUploadResponse;
import dev.videostreaming.microservice.mediaservice.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final S3Service s3Service;
    private final S3Bucket s3Bucket;
    private final Utils utils;


    public CreateUploadResponse createUpload(RemoteUserPrincipal user) {
        return null;
    }
}

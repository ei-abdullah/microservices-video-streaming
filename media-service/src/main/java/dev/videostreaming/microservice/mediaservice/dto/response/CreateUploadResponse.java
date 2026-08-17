package dev.videostreaming.microservice.mediaservice.dto.response;

public record CreateUploadResponse(
        String presignedUrl
) {
}

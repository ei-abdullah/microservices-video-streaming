package dev.videostreaming.microservice.mediaservice.dto.response;

import java.time.Instant;

public record CreateUploadResponse(
        String mediaId,
        String presignedUrl,
        String uploadStatus,
        Instant createdAt,
        Instant updatedAt
) {
}

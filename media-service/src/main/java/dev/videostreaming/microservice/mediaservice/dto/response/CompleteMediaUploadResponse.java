package dev.videostreaming.microservice.mediaservice.dto.response;

import java.time.Instant;

public record CompleteMediaUploadResponse(
    String mediaId,
    String title,
    String failureReason,
    String status,
    Instant createdAt,
    Instant updatedAt
) {
}

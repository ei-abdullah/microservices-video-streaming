package dev.videostreaming.microservice.mediaservice.dto.response;

import java.time.Instant;

public record MediaDetailsResponse(
        String id,
        String title,
        String status,
        String playbackUrl,
        Double duration,
        Integer width,
        Integer height,
        Instant createdAt,
        Instant updatedAt
) {
}

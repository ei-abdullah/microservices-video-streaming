package dev.videostreaming.microservice.authservice.dto.response;

import java.time.Instant;

public record SignupResponse(
        String id,
        String email,
        String token,

        Instant createdAt,
        Instant updatedAt
) {
}
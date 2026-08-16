package dev.videostreaming.microservice.userservice.dto.response;

import common.Role;

import java.time.Instant;
import java.util.List;

public record MeResponse(
        String id,
        String email,
        List<Role> roles,
        Boolean isVerified,
        Instant createdAt,
        Instant updatedAt
) {
}

package common.dto;

import lombok.NonNull;

import java.time.Instant;

public record CreateUserResponse(
        @NonNull String id,
        @NonNull String email,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
) {
}

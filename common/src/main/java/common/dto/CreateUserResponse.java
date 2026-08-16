package common.dto;

import common.Role;
import lombok.NonNull;
import software.amazon.awssdk.annotations.NotNull;

import java.time.Instant;
import java.util.List;

public record CreateUserResponse(
        @NonNull String id,
        @NonNull String email,
        @NonNull List<Role> roles,
        @NotNull Boolean isVerified,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
) {
}

package dev.videostreaming.microservice.authservice.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull String email,
        @NotNull String password
) {
}
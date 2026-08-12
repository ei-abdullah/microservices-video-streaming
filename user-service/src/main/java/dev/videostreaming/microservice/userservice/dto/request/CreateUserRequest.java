package dev.videostreaming.microservice.userservice.dto.request;

public record CreateUserRequest(
        String email,
        String password
) {
}
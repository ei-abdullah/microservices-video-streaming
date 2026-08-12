package dev.videostreaming.microservice.authservice.mapper;

import common.dto.CreateUserResponse;
import dev.videostreaming.microservice.authservice.dto.response.SignupResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public SignupResponse toSignup(CreateUserResponse user, String token) {
        return new SignupResponse(
                user.id(),
                user.email(),
                token,
                user.createdAt(),
                user.updatedAt()
        );
    }
}
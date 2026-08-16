package dev.videostreaming.microservice.userservice.mapper;

import common.Role;
import common.dto.CreateUserResponse;
import dev.videostreaming.microservice.userservice.User;
import dev.videostreaming.microservice.userservice.dto.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUser(CreateUserRequest request) {
        return User
                .builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singletonList(Role.USER))
                .isVerified(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public CreateUserResponse toCreateUser(User user) {
        return new CreateUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles(),
                user.getIsVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
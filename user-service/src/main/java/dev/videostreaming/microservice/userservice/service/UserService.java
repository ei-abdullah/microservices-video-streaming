package dev.videostreaming.microservice.userservice.service;

import common.constant.NotificationConstant;
import common.dto.CreateUserResponse;
import common.dto.GetUserByEmailResponse;
import common.dto.NotificationEvent;
import common.exception.ConflictException;
import common.exception.NotFoundException;
import dev.videostreaming.microservice.userservice.User;
import dev.videostreaming.microservice.userservice.dto.response.MeResponse;
import dev.videostreaming.microservice.userservice.mapper.UserMapper;
import dev.videostreaming.microservice.userservice.dto.request.CreateUserRequest;
import dev.videostreaming.microservice.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetUserByEmailResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new GetUserByEmailResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRoles(),
                        user.getIsVerified()
                ))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("User already exists");
        }

        User user = userMapper.toUser(request);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setIsVerified(false);

        user = userRepository.save(user);

        String verificationUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user/verify")
                .queryParam("verificationToken", verificationToken)
                .toUriString();

        rabbitTemplate.convertAndSend(
                NotificationConstant.EXCHANGE,
                NotificationConstant.ROUTING_KEY_SIGNUP,
                new NotificationEvent(
                        "SIGNUP_VERIFICATION",
                        user.getEmail(),
                        verificationUri,
                        Map.of()
                )
        );

        return userMapper.toCreateUser(user);
    }

    @Transactional
    public void verifyUser(
            String verificationToken
    ) {
        User user = userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(()-> new NotFoundException("User not found"));

        if (user.getIsVerified()) {
            throw new ConflictException("User already verified");
        }

        user.setVerificationToken(null);
        user.setIsVerified(true);

        userRepository.save(user);
    }

    public MeResponse me(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new MeResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRoles(),
                        user.getIsVerified(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .orElseThrow(()-> new NotFoundException("User not found"));
    }
}
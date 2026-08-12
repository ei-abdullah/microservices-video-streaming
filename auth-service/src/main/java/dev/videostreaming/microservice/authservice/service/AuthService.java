package dev.videostreaming.microservice.authservice.service;


import common.dto.CreateUserResponse;
import common.jwt.JwtService;
import dev.videostreaming.microservice.authservice.dto.request.LoginRequest;
import dev.videostreaming.microservice.authservice.dto.request.SignupRequest;
import dev.videostreaming.microservice.authservice.dto.response.LoginResponse;
import dev.videostreaming.microservice.authservice.dto.response.SignupResponse;
import dev.videostreaming.microservice.authservice.mapper.AuthMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final RemoteUserDetailsService userDetailsService;
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;

    public SignupResponse signup(
            SignupRequest request
    ) {
        HttpEntity<SignupRequest> httpEntity = new HttpEntity<>(request);

        ResponseEntity<CreateUserResponse> response = restTemplate.postForEntity(
                "http://user-service/api/v1/user",
                httpEntity,
                CreateUserResponse.class
        );

        CreateUserResponse user = response.getBody();
        String token = jwtService.generateToken(user.email());

        return authMapper.toSignup(
                response.getBody(),
                token
        );
    }

    public LoginResponse login(
            @Valid @NotNull LoginRequest request
    ) {
        String loweredCaseEmail = request.email().toLowerCase().replace(" ", "");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loweredCaseEmail,
                        request.password()
                )
        );

        String token = jwtService.generateToken(authentication.getName());

        return new LoginResponse(token);
    }
}
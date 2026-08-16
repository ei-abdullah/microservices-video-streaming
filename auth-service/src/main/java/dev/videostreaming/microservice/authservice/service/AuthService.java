package dev.videostreaming.microservice.authservice.service;


import common.dto.CreateUserResponse;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import common.jwt.JwtService;
import dev.videostreaming.microservice.authservice.RemoteUserPrincipal;
import dev.videostreaming.microservice.authservice.dto.request.LoginRequest;
import dev.videostreaming.microservice.authservice.dto.request.SignupRequest;
import dev.videostreaming.microservice.authservice.dto.response.LoginResponse;
import dev.videostreaming.microservice.authservice.dto.response.SignupResponse;
import dev.videostreaming.microservice.authservice.mapper.AuthMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;

    public SignupResponse signup(
            SignupRequest request
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SignupRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateUserResponse> response;

        try {
            response = restTemplate.postForEntity(
                    "http://user-service/api/v1/user",
                    httpEntity,
                    CreateUserResponse.class
            );
        } catch (HttpClientErrorException.Forbidden e) {
            throw new BadRequestException("User service rejected the request.");
        }

        CreateUserResponse user = response.getBody();

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        String token = jwtService.generateToken(
                user.id(),
                user.email(),
                user.roles()
        );

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

        RemoteUserPrincipal principal = (RemoteUserPrincipal) authentication.getPrincipal();

        if (principal == null) {
            throw new NotFoundException("User principal not found");
        }

        String token = jwtService.generateToken(
                principal.getId(),
                principal.getUsername(),
                principal.getRoles()
        );

        return new LoginResponse(token);
    }
}
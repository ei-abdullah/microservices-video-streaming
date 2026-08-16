package dev.videostreaming.microservice.userservice.controller;


import common.dto.CreateUserResponse;
import common.dto.GetUserByEmailResponse;
import common.htmlPage.HtmlPageService;
import dev.videostreaming.microservice.userservice.UserPrincipal;
import dev.videostreaming.microservice.userservice.dto.request.CreateUserRequest;
import dev.videostreaming.microservice.userservice.dto.response.MeResponse;
import dev.videostreaming.microservice.userservice.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final HtmlPageService htmlPageService;

    @GetMapping
    public ResponseEntity<GetUserByEmailResponse> getUser(
            @RequestParam String email
    ) {
        GetUserByEmailResponse response = userService.getUserByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @NotNull @RequestBody CreateUserRequest request
    ) {
        CreateUserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUserLandingPage(
            @NotBlank @RequestParam("verificationToken") String verificationToken
    ) {
        String response = htmlPageService.getVerificationLandingPage(verificationToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "text/html")
                .body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(
            @NotBlank @RequestParam("verificationToken") String verificationToken
    ) {
        try {
            userService.verifyUser(verificationToken);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "text/html")
                    .body(htmlPageService.getVerificationSuccessPage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/html")
                    .body(htmlPageService.getVerificationErrorPage(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMe(
            @AuthenticationPrincipal UserPrincipal user
            ) {
        MeResponse response = userService.me(user.getUsername());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
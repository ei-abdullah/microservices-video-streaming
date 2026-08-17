package dev.videostreaming.microservice.mediaservice.controller;

import common.userDetails.RemoteUserPrincipal;
import dev.videostreaming.microservice.mediaservice.dto.response.CreateUploadResponse;
import dev.videostreaming.microservice.mediaservice.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/")
    public ResponseEntity<CreateUploadResponse> createUpload(
            @AuthenticationPrincipal RemoteUserPrincipal user
    ) {
        CreateUploadResponse response = mediaService.createUpload(user);

        return null;
    }

    @PostMapping("/{mediaId}/complete")
    public ResponseEntity<?> completeMediaUpload(@PathVariable String mediaId) {
        return null;
    }

}

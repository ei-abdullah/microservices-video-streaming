package dev.videostreaming.microservice.mediaservice.controller;

import common.s3.S3Service;
import dev.videostreaming.microservice.mediaservice.service.MediaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/getUploadUrl")
    public ResponseEntity<String> getUploadUrl() {
        String response = mediaService.getUploadUrl();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}

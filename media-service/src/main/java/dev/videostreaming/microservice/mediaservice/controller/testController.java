package dev.videostreaming.microservice.mediaservice.controller;

import dev.videostreaming.microservice.mediaservice.config.S3Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class testController {

    private final S3Bucket bucket;

    @GetMapping
    public String test() {
        return bucket.getBucketName();
    }
}

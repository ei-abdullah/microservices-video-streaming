package dev.videostreaming.microservice.transcodingservice.service;

import org.springframework.stereotype.Service;

@Service
public class TranscodingService {
    public void processMedia(String mediaId, String mediaKey, String bucketName) {
        // Get the file from S3 into the memory
        // Process the file using process.builder
        // Upload the processed file back to S3 under proceess-media
        // send notification back to media-service containing the details of processed file
        // Upload meta-data of file
        // mark file as processed ready to be used be end user

    }
}

package dev.videostreaming.microservice.transcodingservice.service;

import common.constant.TranscodingConstant;
import common.dto.TranscodingEvent;
import common.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscodingService {

    private final S3Service s3Service;
    private final RabbitTemplate rabbitTemplate;

    public void processMedia(String mediaId, String mediaKey, String bucketName) {
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("media_" + mediaId + "_");
            Path localInputVideo = tempDir.resolve("input_video.mp4");
            Path hlsOutputDir = tempDir.resolve("hls");
            Files.createDirectories(hlsOutputDir);

            s3Service.downloadFile(mediaKey, bucketName, localInputVideo);

            runFFmpeg(localInputVideo, hlsOutputDir);

            uploadHlsFilesToS3(bucketName, mediaId, hlsOutputDir);

            String masterPlaylistKey = "processed-media/" + mediaId + "/hls/index.m3u8";
            rabbitTemplate.convertAndSend(
                    TranscodingConstant.EXCHANGE,
                    TranscodingConstant.ROUTING_KEY_UPLOAD_MEDIA,
                    new TranscodingEvent(
                            TranscodingConstant.EVENT_PROCESSED_MEDIA,
                            Map.of(
                                    "mediaId", mediaId,
                                    "masterPlaylistKey", masterPlaylistKey,
                                    "bucketName", bucketName,
                                    "status", "READY"
                            )
                    )
            );
            log.info("Published EVENT_PROCESSED_MEDIA for mediaId: {}", mediaId);

        } catch (Exception e) {
            log.error("Transcoding failed for mediaId: {}", mediaId, e);
            throw new RuntimeException("Transcoding failed for mediaId: " + mediaId, e);
        } finally {
            cleanupTempDir(tempDir);
        }
    }

    private void runFFmpeg(Path inputVideo, Path hlsOutputDir) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", inputVideo.toAbsolutePath().toString(),
                "-vf", "scale=-2:720",
                "-c:v", "libx264",
                "-preset", "fast",
                "-crf", "22",
                "-c:a", "aac",
                "-b:a", "128k",
                "-ar", "44100",
                "-g", "48",
                "-keyint_min", "48",
                "-sc_threshold", "0",
                "-hls_time", "4",
                "-hls_playlist_type", "vod",
                "-hls_segment_filename", hlsOutputDir.resolve("segment_%03d.ts").toAbsolutePath().toString(),
                hlsOutputDir.resolve("index.m3u8").toAbsolutePath().toString()
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("FFmpeg: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg process failed with exit code " + exitCode);
        }
    }

    private void uploadHlsFilesToS3(String bucketName, String mediaId, Path hlsOutputDir) throws Exception {
        try (Stream<Path> paths = Files.walk(hlsOutputDir)) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                String relativePath = hlsOutputDir.relativize(file).toString();
                String s3Key = "processed-media/" + mediaId + "/hls/" + relativePath;
                String contentType = file.toString().endsWith(".m3u8")
                        ? "application/vnd.apple.mpegurl"
                        : "video/MP2T";

                s3Service.uploadLocalFile(bucketName, s3Key, file, contentType);
            });
        }
    }

    private void cleanupTempDir(Path tempDir) {
        if (tempDir != null && Files.exists(tempDir)) {
            try (Stream<Path> paths = Files.walk(tempDir)) {
                paths.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (Exception e) {
                log.warn("Failed to clean up temp directory: {}", tempDir, e);
            }
        }
    }
}


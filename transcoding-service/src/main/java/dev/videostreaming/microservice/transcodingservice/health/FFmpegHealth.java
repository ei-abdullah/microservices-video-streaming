package dev.videostreaming.microservice.transcodingservice.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Component
public class FFmpegHealth implements HealthIndicator {

    @Override
    public Health health() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-version");

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            String versionLine;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                versionLine = reader.readLine();
            }

            boolean finished = process
                    .waitFor(3, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return Health
                        .down()
                        .withDetail("error", "FFmpeg command timed out")
                        .build();
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return Health
                        .up()
                        .withDetail("version", versionLine != null ? versionLine : "Unknown")
                        .build();
            } else {
                return Health
                        .down()
                        .withDetail("error", "FFmpeg command failed with exit code " + exitCode)
                        .build();
            }
        } catch (Exception e) {
            return Health
                    .down()
                    .withDetail("error", "FFmpeg is not installed or not found on PATH")
                    .withException(e)
                    .build();
        }
    }
}

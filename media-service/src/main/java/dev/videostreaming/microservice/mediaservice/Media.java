package dev.videostreaming.microservice.mediaservice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String uploaderId;

    private String title;

    private String originalFileName;

    private String contentType;

    private BigDecimal fileSize;

    private String fileExtension;

    private String sourceBucketName;

    private String sourceObjectKey;

    private String processedObjectKey;

    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    private BigDecimal duration;

    private BigDecimal width;

    private BigDecimal height;

    private String failureReason;

    private Instant createdAt;
    private Instant updatedAt;
}

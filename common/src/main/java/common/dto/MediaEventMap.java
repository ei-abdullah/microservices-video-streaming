package common.dto;

import java.io.Serializable;

public record MediaEventMap(
        String mediaId,
        String masterPlaylistKey,
        String bucketName,
        String status,
        String failureReason,
        MediaMetadata metadata
)
implements Serializable {
}

package common.exception;

import java.time.Instant;

public record ApiError(
        String path,
        String message,
        int statusCode,
        Instant timestamp
) {
}

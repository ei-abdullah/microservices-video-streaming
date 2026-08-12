package common.dto;

import java.io.Serializable;
import java.util.Map;

public record NotificationEvent(
        String eventName,
        String email,
        String verificationUri,
        Map<String, Object> vars
) implements Serializable {
}

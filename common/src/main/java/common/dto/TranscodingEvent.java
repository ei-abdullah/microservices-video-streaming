package common.dto;

import java.io.Serializable;
import java.util.Map;

public record TranscodingEvent(
        String eventName,
        Map<String, Object> vars
) implements Serializable {
}

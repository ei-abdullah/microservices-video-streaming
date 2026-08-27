package common.dto;

import java.io.Serializable;
import java.util.Map;

public record MediaEvent(
        String eventName,
        Map<String, Object> vars
) implements Serializable {
}

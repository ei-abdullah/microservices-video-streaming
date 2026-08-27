package common.constant;

public class MediaConstant {
    public static final String EXCHANGE = "media.exchange";
    public static final String QUEUE = "media.queue";
    public static final String ROUTING_KEY = "media.*";

    // ROUTING KEYS
    public static final String ROUTING_KEY_READY_MEDIA = "media.ready";
    public static final String ROUTING_KEY_FAILED_MEDIA = "media.failed";

    // EVENT NAMES
    // Sent by the transcoding service to the media service
    public static final String EVENT_READY_MEDIA = "READY_MEDIA";
    public static final String EVENT_FAILED_MEDIA = "FAILED_MEDIA";

    // CONSTANTS
    public static final String MEDIA_READY = "READY";
    public static final String MEDIA_FAILED = "FAILED";
    public static final String S3_ERROR = "S3_ERROR";
    public static final String QUEUE_FAILED = "QUEUE_FAILED";
}

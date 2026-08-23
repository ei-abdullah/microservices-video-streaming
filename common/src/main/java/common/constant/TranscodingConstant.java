package common.constant;

public class TranscodingConstant {
    public static final String EXCHANGE = "transcoding.exchange";
    public static final String QUEUE = "transcoding.queue";
    public static final String ROUTING_KEY = "transcoding.*";

    // ROUTING KEYS
    public static final String ROUTING_KEY_UPLOAD_MEDIA = "transcoding.upload";

    // EVENT NAMES
    // Sent by the media service to the transcoding service
    public static final String EVENT_ENQUEUE_MEDIA = "ENQUEUE_MEDIA";

    // Sent by the transcoding service to the media service
    public static final String EVENT_PROCESSED_MEDIA = "PROCESSED_MEDIA";
}

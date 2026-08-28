package common;


public class Utils {
    public String getRawMediaKey(String userId, String mediaId) {
        return "raw-media/%s/%s".formatted(userId, mediaId);
    }
}

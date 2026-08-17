package common;


public class Utils {
    public String GetUploadMediaKey(String userId, String mediaId) {
        return "media/%s/%s".formatted(userId, mediaId);
    }
}

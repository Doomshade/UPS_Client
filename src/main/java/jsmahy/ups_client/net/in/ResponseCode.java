package jsmahy.ups_client.net.in;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public enum ResponseCode {
    OK,
    MOVE,
    REJECTED;

    public static ResponseCode getResponseCode(int id) {
        validateId(id);
        return values()[id];
    }

    private static void validateId(final int id) {
        if (id < 0 || id >= values().length) {
            throw new IllegalArgumentException(String.format("Invalid ID response code: %d", id));
        }
    }
}

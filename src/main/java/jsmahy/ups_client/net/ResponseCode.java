package jsmahy.ups_client.net;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public enum ResponseCode implements PacketData {
    NONE,
    OK,
    MOVE,
    REJECTED,
    CONNECT;

    public static ResponseCode getResponseCode(int id) throws IllegalArgumentException {
        validateId(id);
        return values()[id];
    }

    private static void validateId(final int id) throws IllegalArgumentException {
        if (id < 0 || id >= values().length) {
            throw new IllegalArgumentException(String.format("Invalid ID response code: %d", id));
        }
    }

    public static ResponseCode getResponseCode(@NotNull final String s) throws IllegalArgumentException {
        for (ResponseCode rc : values()) {
            if (rc.name().equalsIgnoreCase(s)) {
                return rc;
            }
        }
        throw new IllegalArgumentException(String.format("No response code found for %s!", s));
    }

    public static ResponseCode deserialize(String data, AtomicInteger amountRead) {
        amountRead.addAndGet(data.length());
        return getResponseCode(data);
    }

    @Override
    public String toDataString() {
        return name();
    }
}

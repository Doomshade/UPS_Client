package jsmahy.ups_client.exception;

public class InvalidPacketFormatException extends RuntimeException {
    public InvalidPacketFormatException() {
    }

    public InvalidPacketFormatException(final String message) {
        super(message);
    }

    public InvalidPacketFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidPacketFormatException(final Throwable cause) {
        super(cause);
    }

    public InvalidPacketFormatException(final String message, final Throwable cause,
                                        final boolean enableSuppression,
                                        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

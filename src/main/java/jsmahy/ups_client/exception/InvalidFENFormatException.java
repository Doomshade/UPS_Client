package jsmahy.ups_client.exception;

public class InvalidFENFormatException extends RuntimeException {
    public InvalidFENFormatException() {
    }

    public InvalidFENFormatException(final String message) {
        super(message);
    }

    public InvalidFENFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidFENFormatException(final Throwable cause) {
        super(cause);
    }

    public InvalidFENFormatException(final String message, final Throwable cause,
                                     final boolean enableSuppression,
                                     final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

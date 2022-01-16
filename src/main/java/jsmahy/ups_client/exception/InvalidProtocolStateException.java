package jsmahy.ups_client.exception;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class InvalidProtocolStateException extends RuntimeException {
	public InvalidProtocolStateException() {
	}

	public InvalidProtocolStateException(final String message) {
		super(message);
	}

	public InvalidProtocolStateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InvalidProtocolStateException(final Throwable cause) {
		super(cause);
	}

	public InvalidProtocolStateException(final String message, final Throwable cause, final boolean enableSuppression,
	                                     final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

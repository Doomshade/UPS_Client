package jsmahy.ups_client.net;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public enum ResponseCode implements PacketData {
	/**
	 * The default value -- no response
	 */
	NONE,
	/**
	 * The "OK" response
	 */
	OK,
	/**
	 * The "Rejected" response
	 */
	REJECTED;

	/**
	 * Deserializes the response from the server
	 *
	 * @param data       the response
	 * @param amountRead the amount read from the data
	 *
	 * @return the response
	 *
	 * @throws IllegalArgumentException if an invalid response was received
	 */
	public static ResponseCode deserialize(String data, AtomicInteger amountRead) throws IllegalArgumentException {
		amountRead.addAndGet(data.length());
		return getResponseCode(data);
	}

	/**
	 * Attempts to parse a response from the string
	 *
	 * @param s the string
	 *
	 * @return the response
	 *
	 * @throws IllegalArgumentException if the string is not a response
	 */
	public static ResponseCode getResponseCode(@NotNull final String s) throws IllegalArgumentException {
		for (ResponseCode rc : values()) {
			if (rc.name().equalsIgnoreCase(s)) {
				return rc;
			}
		}
		throw new IllegalArgumentException(String.format("No response code found for %s!", s));
	}

	@Override
	public String toDataString() {
		return name();
	}
}

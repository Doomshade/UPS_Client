package jsmahy.ups_client.net;

import jsmahy.ups_client.util.Square;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The packet data. All implementations of this MUST provide a method with signature "deserialize(String,
 * AtomicInteger)" that deserializes the data and increments the amount read from the String to the AtomicInteger
 * parameter.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @see ResponseCode#deserialize(String, AtomicInteger)
 * @see Square#deserialize(String, AtomicInteger)
 * @since 1.0
 */
public interface PacketData {

	/**
	 * @return The String representation of this data
	 */
	String toDataString();
}

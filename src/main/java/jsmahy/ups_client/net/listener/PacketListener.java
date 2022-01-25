package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.in.PacketIn;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketListener {

	/**
	 * Handles the received packet by the given handler in the current state
	 * @param packet the packet to handle
	 * @throws InvalidProtocolStateException if the packet was not found in this state
	 */
	void handle(PacketIn packet) throws InvalidProtocolStateException;
}

package jsmahy.ups_client.net;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.util.AlertBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Deserializes the incoming traffic from the server
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketDeserializer implements Runnable {
	private static final Logger L = LogManager.getLogger(PacketDeserializer.class);

	/**
	 * The messages from server that are received by this input stream.
	 */
	private final InputStream in;
	private final BufferedPacket bufferedPacket = new BufferedPacket();

	private boolean stop = false;

	public PacketDeserializer(final InputStream in) {
		this.in = in;
	}

	@Override
	public void run() {
		while (!stop) {
			String s;
			int buffered;

			try {
				// read the buffer
				byte[] buf = new byte[4096];
				try {
					// if the read returns <0, that means error -> the server is unreachable, disconnect the player
					// -- the server likely cut the connection
					if (in.read(buf) < 0) {
						L.fatal("Server is unreachable, disconnecting...");
						Platform.runLater(() -> new AlertBuilder(Alert.AlertType.INFORMATION)
								.title("Connectivity issues")
								.header("Server unreachable")
								.content("Failed to receive a packet from the server for a while, attempting to " +
										"reconnect...")
								.build()
								.show());
						NetworkManager.getInstance()
								.disconnect("Connection error", "Server connection error",
										"Disconnected", true);
						break;
					}
				}
				// the server has not responded in a long time, an exception is thrown as a cause to that
				// attempt to reconnect the player to the server
				catch (SocketException | SocketTimeoutException e) {
					L.fatal("Server is unreachable (ping >20000ms), attempting to reconnect...");

					Platform.runLater(() -> new AlertBuilder(Alert.AlertType.INFORMATION)
							.title("Connectivity issues")
							.header("Server unreachable")
							.content("Attempting to reconnect...")
							.build()
							.show());
					NetworkManager.getInstance()
							.disconnect("Connection error", "Server connection error",
									"Disconnected", false);
					Client.attemptReconnect();
					break;
				}
				// an IO exception not related to timeout occurred, disconnect the player
				catch (IOException e) {
					L.fatal("Disconnected.");
					Platform.runLater(() -> new AlertBuilder(Alert.AlertType.INFORMATION)
							.title("Connectivity issues")
							.header("Server unreachable")
							.content("Disconnected, attempting to reconnect...")
							.build()
							.show());
					break;
				}

				// parse the packet
				s = new String(buf, StandardCharsets.UTF_8);
				s = s.trim();
				while (true) {
					// append the data to the packet
					buffered = bufferedPacket.append(s);

					// if the packet is not yet ready, don't attempt to parse it
					if (!bufferedPacket.isPacketReady()) {
						break;
					}

					// the data is fully buffered -> the packet is ready
					// we can handle the packet now
					L.debug("Handling packet " + bufferedPacket);

					// receive the buffered packet and delegate it to the listeners
					NetworkManager.getInstance().receivePacket(bufferedPacket);

					// reset the buffer and check if there's more in the buffers
					bufferedPacket.reset();
					if (buffered >= s.length()) {
						break;
					}

					// there's more data in the buffer, shift the string by the bytes read and attempt to parse it
					// once more
					try {
						s = s.substring(buffered);
					} catch (Exception e) {
						L.error("An exception occurred when reading another packet");
						break;
					}
				}
			}
			// the packet send was invalid, disconnect the player
			catch (InvalidPacketFormatException | InvalidProtocolStateException e) {
				L.fatal("Received an invalid packet or the client is in an invalid state");
				NetworkManager.getInstance()
						.disconnect("Invalid packet", "Invalid packet received",
								"The server sent an invalid packet or the client is in an invalid state", true);
				break;
			}
			// some internal exception happened, disconnect the player
			catch (Exception e) {
				L.fatal("An error occurred when handling a packet", e);
				NetworkManager.getInstance()
						.disconnect("Connection error", "Server connection error",
								"An unknown error occurred when handling a packet", true);
				break;
			}
		}
	}
}

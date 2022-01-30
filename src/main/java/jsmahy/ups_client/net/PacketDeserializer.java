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
				byte[] buf = new byte[4096];
				try {
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
				} catch (SocketException | SocketTimeoutException e) {
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
				} catch (IOException e) {
					L.fatal("Disconnected.");
					Platform.runLater(() -> new AlertBuilder(Alert.AlertType.INFORMATION)
							.title("Connectivity issues")
							.header("Server unreachable")
							.content("Disconnected, attempting to reconnect...")
							.build()
							.show());
					break;
				}
				s = new String(buf, StandardCharsets.UTF_8);
				s = s.trim();
				while (true) {
					buffered = bufferedPacket.append(s);

					if (!bufferedPacket.isPacketReady()) {
						break;
					}
					// the data is fully buffered -> the packet is ready
					// we can handle the packet now
					L.debug("Handling packet " + bufferedPacket);

					NetworkManager.getInstance().receivePacket(bufferedPacket);

					// reset the buffer and check if there's more in the buffer
					bufferedPacket.reset();
					if (buffered >= s.length()) {
						break;
					}

					// shift the string by the bytes read
					try {
						s = s.substring(buffered);
					} catch (Exception e) {
						L.error("An exception occurred when reading another packet");
						break;
					}
				}
			} catch (InvalidPacketFormatException | InvalidProtocolStateException e) {
				L.fatal("Received an invalid packet or the client is in an invalid state");
				NetworkManager.getInstance()
						.disconnect("Invalid packet", "Invalid packet received",
								"The server sent an invalid packet or the client is in an invalid state", true);
				break;
			} catch (Exception e) {
				L.fatal("An error occurred when handling a packet", e);
				NetworkManager.getInstance()
						.disconnect("Connection error", "Server connection error",
								"An unknown error occurred when handling a packet", true);
				break;
			}
		}
	}
}

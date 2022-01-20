package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.listener.impl.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class PacketDeserializer implements Runnable {
	private static final Logger L = LogManager.getLogger(PacketDeserializer.class);

	/**
	 * The messages from server that are received by this input stream.
	 */
	private final InputStream in;
	private final BufferedPacket bufferedPacket = new BufferedPacket();

	public PacketDeserializer(final InputStream in) {
		this.in = in;
	}

	@Override
	public void run() {
		while (true) {

			String s;
			int buffered;
			try {
				byte[] buf = new byte[1024];
				while (in.read(buf) == -1) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				s = new String(buf, StandardCharsets.UTF_8);
				s = s.trim();
				while (true) {
					buffered = bufferedPacket.append(s);
					// the data is fully buffered -> the packet is ready
					// we can handle the packet now
					if (bufferedPacket.isPacketReady()) {
						L.debug("Handling packet " + bufferedPacket);

						// 0-0x7F = packet out, 0x80-0xFF = packet in
						if (bufferedPacket.getPacketId() < ProtocolState.PACKET_IN_OFFSET) {
							L.debug("Queueing " + bufferedPacket);
							NetworkManager.getInstance().sendPacket(bufferedPacket);
						} else if (bufferedPacket.getPacketId() >= 0) {
							L.debug("Received " + bufferedPacket);
							NetworkManager.getInstance().receivePacket(bufferedPacket);
						}
						bufferedPacket.reset();
					} else {
						break;
					}
					if (buffered < s.length()) {
						try {
							s = s.substring(buffered);
						} catch (Exception e) {
							break;
						}
					} else {
						break;
					}
				}
			} catch (IOException e) {
                L.fatal("Packet did not reach the server", e);
				NetworkManager.getInstance().disconnect("Connection error", "Server connection error", "A packet " +
						"could not reach the server: " + e.getMessage());
				L.info("Disconnecting...");
				break;
			} catch (InvalidPacketFormatException | InvalidProtocolStateException e) {
				L.fatal(e);
				bufferedPacket.reset();
				// TODO
				// L.info("Disconnecting...");
				// break;
			}
		}
	}
}

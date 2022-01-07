package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListener;
import jsmahy.ups_client.net.listener.impl.PlayListener;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

public class PacketDeserializer implements Runnable {
    private static final String PACKET_MAGIC = "CHESS";
    private static final int PACKET_HEADER_LENGTH = PACKET_MAGIC.length() + 5;
    private static final String PACKET_FORMAT = "%02x%03d%s";

    /**
     * The logger.
     */
    private static final Logger L = LogManager.getLogger(PacketDeserializer.class);
    /**
     * The NetworkManager instance.
     */
    private static final NetworkManager NM = NetworkManager.getInstance();

    /**
     * The messages from server that are received by this input stream.
     */
    private final InputStream in;
    private final PlayListener client;
    private final StringBuilder sb = new StringBuilder(PACKET_HEADER_LENGTH);
    private final BufferedPacket bufferedPacket = new BufferedPacket();

    public PacketDeserializer(final InputStream in, final PlayListener client) {
        this.in = in;
        this.client = client;
        final String message = "TESTERONIKIASD\n";
        System.out.printf(PACKET_FORMAT, 0xa0, message.length(), message);
    }

    @Override
    public void run() {
        while (true) {

            String s;
            int buffered;
            try {
                byte[] buf = new byte[4096];
                if (in.read(buf) < 0) {
                    // TODO this could cause some problems? idk
                    continue;
                }
                s = new String(buf, StandardCharsets.UTF_8);
                s = s.trim();
                while (true) {
                    buffered = bufferedPacket.append(s);
                    if (bufferedPacket.isPacketReady()) {
                        L.debug("Received " + bufferedPacket);
                        // TODO call the packet ig
                        final PacketIn<? extends PacketListener> p = (PacketIn<? extends PacketListener>)
                                NM.getState().getPacket(bufferedPacket.getPacketId());
                        p.broadcast(NM.getCurrentListener());
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
            } catch (IOException | InvalidPacketFormatException e) {
                L.fatal(e);
                bufferedPacket.reset();
                break;
            }
        }
        client.disconnect("Attempted to send an invalid packet");
    }

    /**
     * Attempts to read the packet.
     *
     * @param in     the string of bytes
     * @param packet the packet handler
     */
    private void readPacket(final String in, final PacketIn<? extends PacketListener> packet)
            throws InvalidPacketFormatException {
        final String[] split = in.substring(3).split(String.valueOf(
                Util.SEPARATION_CHAR));
        if (split.length == 0) {
            throw new InvalidPacketFormatException("No values received after packet ID!");
        }
        L.debug(format("Parsing %s arguments...", String.join(", ", split)));
    }

    /**
     * Attempts to look up the packet with the given ID.
     *
     * @param packetId the packet ID
     * @return the packet or {@code null} if it does not exist
     */
    private PacketIn<? extends PacketListener> getPacket(final int packetId)
            throws InvalidPacketFormatException {
        return (PacketIn<? extends PacketListener>)
                NM.getState().getPacket(packetId);
    }

    private int parsePacketId(final String s) throws NumberFormatException {
        return Integer.parseUnsignedInt(s.substring(0, 2), 16);
    }
}

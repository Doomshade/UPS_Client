package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListener;
import jsmahy.ups_client.net.listener.PlayerConnection;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;

public class PacketDeserializer implements Runnable {
    public static final int MAX_PACKET_SIZE = 512;
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
    private final PlayerConnection client;

    public PacketDeserializer(final InputStream in, final PlayerConnection client) {
        this.in = in;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            final String s;
            try {
                s = readPacket();
            } catch (IOException | InvalidPacketFormatException e) {
                L.fatal(e);
                break;
            }
            // ID | data

            // parse the packet ID
            final int packetId;
            try {
                packetId = parsePacketId(s);
            } catch (NumberFormatException e) {
                L.fatal("Could not read the packet ID!", e);
                break;
            }

            // attempt to look up the packet with the given ID and state
            final PacketIn<? extends PacketListener> packet;
            try {
                packet = getPacket(packetId);
            } catch (InvalidPacketFormatException e) {
                L.fatal("Received packet was not found in the lookup table!", e);
                break;
            }

            // attempt to read the data section
            try {
                readPacket(s, packet);
            } catch (InvalidPacketFormatException e) {
                L.fatal("Could not read the packet from the input stream!", e);
                break;
            }

            // packet is valid and data is read, broadcast it to the listener
            L.info(format("Received %s packet, broadcasting to the listeners...", packet));
            try {
                packet.broadcast(NM.getCurrentListener());
            } catch (InvalidPacketFormatException e) {
                L.fatal(e);
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
        packet.read(split);
    }

    /**
     * Attempts to look up the packet with the given ID.
     *
     * @param packetId the packet ID
     *
     * @return the packet or {@code null} if it does not exist
     */
    private PacketIn<? extends PacketListener> getPacket(final int packetId)
            throws InvalidPacketFormatException {
        final PacketIn<? extends PacketListener> packet = (PacketIn<? extends PacketListener>)
                NM.getState().getPacket(PacketDirection.CLIENT_BOUND, packetId);
        L.debug(format("Found packet with ID %d and state %s", packetId, NM.getState()));
        return packet;
    }

    private int parsePacketId(final String s) throws NumberFormatException {
        final int packetId = Integer.parseUnsignedInt(s.substring(0, 2), 16);
        L.debug("Deserialized packet ID: " + packetId);
        return packetId;
    }

    private String readPacket() throws IOException, InvalidPacketFormatException {
        final byte[] buf = new byte[MAX_PACKET_SIZE];
        int read = in.read(buf);
        if (read < 3) {
            throw new InvalidPacketFormatException("Invalid packet received! - packet " +
                    "length: " + read);
        }
        return new String(buf, 0, read).trim();
    }

    private String getString() {
        final byte[] s = new byte[MAX_PACKET_SIZE];
        int read = -1;
        try {
            read = in.read(s);
        } catch (IOException e) {
            L.error(e);
        }
        if (read < 3) {
            L.error("Invalid packet received! - invalid packet length: " + read);
            return null;
        }
        return new String(s, 0, read).trim();
    }
}

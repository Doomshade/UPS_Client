package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.in.PacketListener;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static java.lang.String.format;

public class PacketDeserializer implements Runnable {
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
    private final Scanner in;

    public PacketDeserializer(final InputStream in) {
        this.in = new Scanner(in);
    }

    @Override
    public void run() {
        while (true) {
            final String s = in.nextLine();
            if (s.length() < 3) {
                L.error("Invalid packet received! - invalid packet length: " + s.length());
                continue;
            }
            // ID | state | data

            // TODO redo to UTF
            // read the packet ID
            final int packetId;
            try {
                packetId = Integer.parseUnsignedInt(s.substring(0, 2), 16);
                L.debug("Deserialized packet ID: " + packetId);
            } catch (NumberFormatException e) {
                L.fatal("Could not read the packet ID!", e);
                continue;
            }

            // attempt to look up the packet with the given ID and state
            final PacketIn<? extends PacketListener> packet;
            try {
                packet = (PacketIn<? extends PacketListener>)
                        NM.getState().getPacket(PacketDirection.CLIENT_BOUND, packetId);
                L.debug(format("Found packet with ID %d and state %s", packetId, NM.getState()));
            } catch (InvalidPacketFormatException e) {
                // TODO disconnect the client
                L.fatal("Received packet was not found in the lookup table!", e);
                continue;
            }

            // attempt to parse the data section
            try {
                final String[] split = s.substring(3).split(String.valueOf(
                        Util.SEPARATION_CHAR));
                if (split.length == 0) {
                    throw new InvalidPacketFormatException("No values received after packet ID!");
                }
                L.debug(format("Parsing %s arguments...", String.join(", ", split)));
                packet.read(split);
                L.debug(format("Successfully read %s packet from the input stream", packet));
            } catch (IOException | InvalidPacketFormatException e) {
                L.fatal("Could not read the packet from the input stream!", e);
                continue;
            }

            L.debug(format("Received %s packet, broadcasting to the listeners...", packet));
            packet.broadcast(NM.getCurrentListener());
        }
    }
}

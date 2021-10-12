package jsmahy.ups_client.net.in;

import jsmahy.ups_client.HelloApplication;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.PacketDirection;
import jsmahy.ups_client.net.ProtocolState;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;

import static java.lang.String.format;

public class PacketDeserializer implements Runnable{
    /**
     * The logger.
     */
    private static final Logger L = HelloApplication.getLogger();

    /**
     * The NetworkManager instance.
     */
    private static final NetworkManager NM = NetworkManager.getInstance();

    /**
     * The messages from server that are received by this input stream.
     */
    private final DataInputStream in;

    public PacketDeserializer(final DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {
            // ID | state | data

            // read the packet ID
            final int packetId;
            try {
                packetId = in.readUnsignedByte();
            } catch (IOException e) {
                L.fatal("Could not read the packet ID from the input stream!", e);
                return;
            }

            // read the protocol state
            final int state;
            try {
                state = in.readUnsignedByte();
            } catch (IOException e) {
                L.fatal("Could not read the protocol state from the input stream!", e);
                return;
            }

            // check if the states match
            // this should not happen and an exception should be thrown
            final ProtocolState serverState = ProtocolState.getById(state);
            final ProtocolState clientState = NM.getState();
            if (clientState != serverState) {
                L.error(format(
                        "Server sent a packet in state %s, while the client has the state" +
                                " %s!", serverState, clientState));
                try {
                    in.skipBytes(in.available());
                } catch (IOException e) {
                    L.fatal("Failed to skip bytes!", e);
                }
                return;
            }

            // attempt to look up the packet with the given ID and state
            final PacketIn<? extends PacketListener> packet;
            try {
                packet = (PacketIn<? extends PacketListener>) serverState.getPacket(
                        PacketDirection.CLIENT_BOUND,
                        packetId);
                L.debug(format("Found packet with ID %d and state %s", packetId,
                        serverState));
            } catch (InvalidPacketFormatException e) {
                // TODO disconnect the client
                L.fatal("Received packet was not found in the lookup table!", e);
                return;
            }

            // attempt to parse the data section
            try {
                packet.read(in);
                L.debug(format("Successfully read %s packet from the input stream", packet));
            } catch (IOException | InvalidPacketFormatException e) {
                L.fatal("Could not read the packet from the input stream!", e);
                return;
            }

            L.debug(format("Received %s packet, broadcasting to the listeners...", packet));
            packet.broadcast(NM.getCurrentListener());
        }
    }
}

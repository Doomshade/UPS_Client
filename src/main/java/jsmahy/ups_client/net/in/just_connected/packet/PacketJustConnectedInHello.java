package jsmahy.ups_client.net.in.just_connected.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.just_connected.PacketInJustConnected;
import jsmahy.ups_client.net.listener.PacketListenerJustConnected;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This packet is received when the player attempts to handshake with the server either by
 * joining the queue or reconnecting to the game.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketJustConnectedInHello implements PacketInJustConnected {
    private ResponseCode responseCode = ResponseCode.NONE;

    @Override
    public void read(final String in) throws InvalidPacketFormatException {
        // we only need the last bit right now as we only have two response codes
        try {
            this.responseCode = ResponseCode.getResponseCode(in);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }

    }

    @Override
    public void broadcast(final PacketListenerJustConnected listener) throws InvalidPacketFormatException {
        listener.onHello(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("responseCode", responseCode)
                .toString();
    }
}

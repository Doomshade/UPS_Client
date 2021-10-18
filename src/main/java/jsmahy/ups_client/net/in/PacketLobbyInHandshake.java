package jsmahy.ups_client.net.in;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This packet is received when the player attempts to handshake with the server either by
 * joining the queue or reconnecting to the game.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketLobbyInHandshake implements PacketInLobby {
    private ResponseCode responseCode = ResponseCode.NONE;
    private String reason = "";

    @Override
    public void read(final String[] in) {
        // we only need the last bit right now as we only have two response codes
        this.responseCode = ResponseCode.getResponseCode(in[0]);
    }

    @Override
    public void broadcast(final PacketListenerLobby listener) {
        listener.onHandshake(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("responseCode", responseCode)
                .toString();
    }
}

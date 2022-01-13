package jsmahy.ups_client.net.in.just_connected.packet;

import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.just_connected.PacketInJustConnected;
import jsmahy.ups_client.net.PacketDataField;
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
    @PacketDataField
    private ResponseCode responseCode = ResponseCode.NONE;

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

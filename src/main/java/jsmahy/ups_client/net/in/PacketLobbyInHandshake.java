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
    private ResponseCode responseCode = null;
    private boolean white = true;
    private String opponentName = "";
    private String fenString = "";

    @Override
    public void read(final String[] in) throws InvalidPacketFormatException {
        // we only need the last bit right now as we only have two response codes
        this.responseCode = ResponseCode.getResponseCode(in[0]);
        if (this.responseCode == ResponseCode.RECONNECT) {
            if (in.length < 4){
                throw new InvalidPacketFormatException("Invalid packet length received!");
            }
            this.white = in[1].equalsIgnoreCase("W");
            this.opponentName = in[2];
            this.fenString = in[3];
        }
    }

    @Override
    public void broadcast(final PacketListenerLobby listener) {
        listener.onHandshake(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getFenString() {
        return fenString;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isWhite() {
        return white;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("responseCode", responseCode)
                .toString();
    }
}

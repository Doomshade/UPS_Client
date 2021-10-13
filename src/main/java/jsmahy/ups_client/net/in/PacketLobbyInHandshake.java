package jsmahy.ups_client.net.in;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.DataInputStream;
import java.io.IOException;

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
    public void read(final DataInputStream in) throws IOException {
        // we only need the last bit right now as we only have two response codes
        this.responseCode = ResponseCode.getResponseCode(in.readUTF());
        if (this.responseCode == ResponseCode.RECONNECT) {
            this.white = in.readUTF().equalsIgnoreCase("W");
            this.opponentName = in.readUTF();
            this.fenString = in.readUTF();
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

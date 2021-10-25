package jsmahy.ups_client.net.in;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.listener.PacketListenerLobby;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketLobbyInGameStart implements PacketInLobby {
    private ResponseCode responseCode = null;
    private boolean white = true;
    private String opponentName = "";
    private String fenString = "";

    @Override
    public void read(final String[] in) throws InvalidPacketFormatException {
        try {
            this.responseCode = ResponseCode.getResponseCode(in[0]);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }

        if (this.responseCode == ResponseCode.CONNECT) {
            if (in.length < 4) {
                throw new InvalidPacketFormatException("Invalid packet length received!");
            }
            this.white = in[1].equalsIgnoreCase("W");
            this.opponentName = in[2];
            this.fenString = in[3];
        }
    }

    @Override
    public void broadcast(final PacketListenerLobby listener) throws InvalidPacketFormatException {
        listener.onGameStart(this);
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
                .append("white", white)
                .append("opponentName", opponentName)
                .append("fenString", fenString)
                .toString();
    }
}

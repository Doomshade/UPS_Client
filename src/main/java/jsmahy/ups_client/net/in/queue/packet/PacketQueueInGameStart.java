package jsmahy.ups_client.net.in.queue.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.queue.PacketInQueue;
import jsmahy.ups_client.net.listener.PacketListenerQueue;
import jsmahy.ups_client.util.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketQueueInGameStart implements PacketInQueue {
    private ResponseCode responseCode = null;
    private boolean white = true;
    private String opponentName = "";
    private String fenString = Util.START_FEN;

    @Override
    public void read(String in) throws InvalidPacketFormatException {
        this.white = in.toUpperCase().charAt(0) == '0';
        this.opponentName = in.substring(1);
    }

    @Override
    public void broadcast(PacketListenerQueue listener) throws InvalidPacketFormatException {
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

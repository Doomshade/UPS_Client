package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.net.listener.PacketListenerPlay;

public class PacketPlayInOpponentName implements PacketInPlay {
    private String opponentName = "";

    @Override
    public void read(String in) throws InvalidPacketFormatException {
        this.opponentName = in;
    }

    @Override
    public void broadcast(PacketListenerPlay listener) throws InvalidPacketFormatException {
        listener.onOpponentName(this);
    }

    public String getOpponentName() {
        return opponentName;
    }
}

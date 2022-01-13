package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;

public class PacketPlayInOpponentName implements PacketInPlay {
    @PacketDataField
    private String opponentName = "";

    public String getOpponentName() {
        return opponentName;
    }
}

package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;

public class PacketPlayInGameFinish implements PacketInPlay {
    @PacketDataField
    private int finishType = -1;

    public int getFinishType() {
        return finishType;
    }
}

package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketPlayInGameFinish implements PacketInPlay {
    @PacketDataField
    private int finishType = -1;

    public int getFinishType() {
        return finishType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("finishType", finishType)
                .toString();
    }
}

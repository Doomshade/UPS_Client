package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketPlayInOpponentName implements PacketInPlay {
    @PacketDataField
    private String opponentName = "";

    public String getOpponentName() {
        return opponentName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("opponentName", opponentName)
                .toString();
    }
}

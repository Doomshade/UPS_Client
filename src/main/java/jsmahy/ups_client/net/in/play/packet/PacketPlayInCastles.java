package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketPlayInCastles implements PacketInPlay {
    @PacketDataField(1)
    private boolean white = false;
    @PacketDataField(2)
    private boolean longCastles = false;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("white", white)
                .append("longCastles", longCastles)
                .toString();
    }
}

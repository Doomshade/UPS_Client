package jsmahy.ups_client.net.out.play;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.out.PacketOut;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This packet is sent whenever the player makes a move
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutMove implements PacketOut {
    @PacketDataField(1)
    private final Square from;
    @PacketDataField(2)
    private final Square to;

    public PacketPlayOutMove(final Square from, final Square to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .toString();
    }
}

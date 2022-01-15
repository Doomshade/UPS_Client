package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The opponent moved a piece
 */
public class PacketPlayInMove implements PacketInPlay {
    @PacketDataField(1)
    private Square from = new Square(0, 0);
    @PacketDataField(2)
    private Square to = new Square(0, 0);

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .toString();
    }
}

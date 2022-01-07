package jsmahy.ups_client.net.out.play;

import jsmahy.ups_client.net.out.PacketOut;
import jsmahy.ups_client.util.Square;
import jsmahy.ups_client.util.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This packet is sent whenever the player makes a move
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutMove implements PacketOut {
    private final Square from, to;

    public PacketPlayOutMove(final Square from, final Square to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void write(final BufferedOutputStream out) throws IOException {
        out.write(from.toAsciiString()
                .concat(String.valueOf(Util.SEPARATION_CHAR))
                .concat(to.toAsciiString())
                .getBytes(StandardCharsets.UTF_8));
        // short pos = (short) ((from.getRow() << 9) | (from.getColumn() << 6) | (to.getRow() <<
        // 3) | to.getColumn());
        // out.writeShort(pos);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .toString();
    }
}

package jsmahy.ups_client.net.out;

import jsmahy.ups_client.util.Position;
import jsmahy.ups_client.util.Util;

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
    private final Position from, to;

    public PacketPlayOutMove(final Position from, final Position to) {
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
}

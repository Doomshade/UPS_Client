package jsmahy.ups_client.net.out;

import jsmahy.ups_client.util.Position;

import java.io.DataOutputStream;
import java.io.IOException;

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
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(from.toAsciiString().concat(to.toAsciiString()));
        // short pos = (short) ((from.getRow() << 9) | (from.getColumn() << 6) | (to.getRow() <<
        // 3) | to.getColumn());
        // out.writeShort(pos);
    }
}

package jsmahy.ups_client.net;

import jsmahy.ups_client.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MovePacket extends AbstractPacket {
    private Position from;
    private Position to;

    public MovePacket() {
        super(0x10);
    }

    public MovePacket(Position from, Position to) {
        this();
        this.from = from;
        this.to = to;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(from.getRow());
        out.writeInt(from.getColumn());
        out.writeInt(to.getRow());
        out.writeInt(to.getColumn());
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int fromRow = in.readInt();
        int fromColumn = in.readInt();
        int toRow = in.readInt();
        int toColumn = in.readInt();
        from = new Position(fromRow, fromColumn);
        to = new Position(toRow, toColumn);
    }
}

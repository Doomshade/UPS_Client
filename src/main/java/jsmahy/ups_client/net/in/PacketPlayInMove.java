package jsmahy.ups_client.net.in;

import jsmahy.ups_client.util.Position;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInMove implements PacketInPlay {
    private Position from = null;
    private Position to = null;
    private ResponseCode responseCode = ResponseCode.REJECTED;

    public PacketPlayInMove() {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.responseCode = ResponseCode.getResponseCode(in.readUTF());
        final String pos = in.readUTF();
        if (pos.length() != 4) {
            throw new IOException("Server sent an invalid position packet!");
        }

        // the server sent us back that the move was valid
        if (this.responseCode == ResponseCode.OK) {
            return;
        }
        this.from = Position.fromString(pos.substring(0, 2));
        this.to = Position.fromString(pos.substring(2, 4));

        /*byte response = (byte) (in.readByte() & 0b11);
        responseCode = ResponseCode.values()[response];
        if (responseCode == ResponseCode.OK) {
            return;
        }
        short position = in.readShort();
        byte fromX = (byte) ((position >> 9) & 0b111);
        byte fromY = (byte) ((position >> 6) & 0b111);
        byte toX = (byte) ((position >> 3) & 0b111);
        byte toY = (byte) (position & 0b111);

        from = new Position(fromX, fromY);
        to = new Position(toX, toY);*/
    }

    @Override
    public void broadcast(final PacketListenerPlay listener) {
        listener.onMove(this);
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}

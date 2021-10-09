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
        byte response = (byte) (in.readByte() & 0b11);
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
        to = new Position(toX, toY);
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

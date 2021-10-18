package jsmahy.ups_client.net.in;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.util.Position;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketPlayInMove implements PacketInPlay {
    private Position from = null;
    private Position to = null;
    private ResponseCode responseCode = ResponseCode.REJECTED;

    public PacketPlayInMove() {
    }

    @Override
    public void read(String[] in) throws InvalidPacketFormatException {
        if (in.length < 3) {
            throw new InvalidPacketFormatException("Invalid packet size received!");
        }
        try {
            this.responseCode = ResponseCode.getResponseCode(in[0]);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }

        // the server sent us back that the move was valid
        if (this.responseCode == ResponseCode.OK) {
            return;
        }
        try {
            this.from = Position.fromString(in[1]);
            this.to = Position.fromString(in[2]);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .append("responseCode", responseCode)
                .toString();
    }
}

package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketPlayInMove implements PacketInPlay {
    @PacketDataField(1)
    private Square from = null;
    @PacketDataField(2)
    private Square to = null;
    private ResponseCode responseCode = ResponseCode.REJECTED;

    public PacketPlayInMove() {
    }

    public void read(String in) throws InvalidPacketFormatException {
        // the move was invalid
        if (in.length() == 0) {
            responseCode = ResponseCode.REJECTED;
            return;
        }

        // we expect the length of the packet to be 4
        if (in.length() != 4) {
            throw new InvalidPacketFormatException("The packet length is invalid!");
        }
        try {
            this.from = Square.fromString(in.substring(0, 2));
            this.to = Square.fromString(in.substring(2, 4));
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
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

package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.net.listener.PacketListenerPlay;

public class PacketPlayInDrawOffer implements PacketInPlay {
    private ResponseCode responseCode = ResponseCode.NONE;

    @Override
    public void read(final String[] in) throws InvalidPacketFormatException {
        try {
            this.responseCode = ResponseCode.getResponseCode(in[0]);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }
    }

    @Override
    public void broadcast(final PacketListenerPlay listener) throws InvalidPacketFormatException {
        listener.onDrawOffer(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}

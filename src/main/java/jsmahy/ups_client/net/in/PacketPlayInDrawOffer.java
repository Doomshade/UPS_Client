package jsmahy.ups_client.net.in;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;

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
    public void broadcast(final PacketListenerPlay listener) {
        listener.onDrawOffer(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}

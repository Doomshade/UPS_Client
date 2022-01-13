package jsmahy.ups_client.net.in.logged_in.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.PacketInLoggedIn;
import jsmahy.ups_client.net.listener.PacketListenerLoggedIn;

public class PacketLoggedInInJoinQueue implements PacketInLoggedIn {
    private ResponseCode rc = ResponseCode.NONE;

    @Override
    public void read(String in) throws InvalidPacketFormatException {
        try {
            this.rc = ResponseCode.getResponseCode(in);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }
    }

    @Override
    public void broadcast(PacketListenerLoggedIn listener) throws InvalidPacketFormatException {
        listener.onQueue(this);
    }

    public ResponseCode getResponseCode() {
        return rc;
    }
}

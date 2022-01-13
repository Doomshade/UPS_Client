package jsmahy.ups_client.net.in.logged_in.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.PacketInLoggedIn;

public class PacketLoggedInInJoinQueue implements PacketInLoggedIn {
    @PacketDataField
    private ResponseCode rc = ResponseCode.NONE;

    public ResponseCode getResponseCode() {
        return rc;
    }
}

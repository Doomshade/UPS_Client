package jsmahy.ups_client.net.in.logged_in.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.PacketInLoggedIn;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketLoggedInInJoinQueue implements PacketInLoggedIn {
    @PacketDataField
    private ResponseCode rc = ResponseCode.NONE;

    public ResponseCode getResponseCode() {
        return rc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("rc", rc)
                .toString();
    }
}

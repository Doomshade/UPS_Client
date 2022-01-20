package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketPlayInDrawOffer implements PacketInPlay {
    @PacketDataField
    private ResponseCode responseCode = ResponseCode.NONE;

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("responseCode", responseCode)
                .toString();
    }
}

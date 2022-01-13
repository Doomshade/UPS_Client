package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.play.PacketInPlay;

public class PacketPlayInDrawOffer implements PacketInPlay {
    @PacketDataField
    private ResponseCode responseCode = ResponseCode.NONE;

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}

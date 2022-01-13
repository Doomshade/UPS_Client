package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;

public class PacketPlayInMessage implements PacketInPlay {

    @PacketDataField
    private String message = "";

    public String getMessage() {
        return message;
    }
}

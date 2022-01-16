package jsmahy.ups_client.net.out.play;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.out.PacketOut;

public class PacketPlayOutMessage implements PacketOut {

    @PacketDataField
    private final String message;

    public PacketPlayOutMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PacketPlayOutMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}

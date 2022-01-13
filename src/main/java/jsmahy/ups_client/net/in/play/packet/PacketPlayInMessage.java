package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.net.listener.PacketListenerPlay;

public class PacketPlayInMessage implements PacketInPlay {

    private String message = "";
    @Override
    public void read(String in) throws InvalidPacketFormatException {
        this.message = in;
    }

    @Override
    public void broadcast(PacketListenerPlay listener) throws InvalidPacketFormatException {
        listener.onMessage(this);
    }

    public String getMessage() {
        return message;
    }
}

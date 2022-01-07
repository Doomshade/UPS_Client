package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.net.listener.PacketListenerPlay;

public class PacketPlayInGameFinish implements PacketInPlay {
    @Override
    public void read(String in) throws InvalidPacketFormatException {
        // TODO
    }

    @Override
    public void broadcast(PacketListenerPlay listener) throws InvalidPacketFormatException {
        // TODO
    }
}

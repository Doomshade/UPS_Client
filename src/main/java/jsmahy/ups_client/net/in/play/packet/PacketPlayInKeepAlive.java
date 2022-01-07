package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.net.listener.PacketListenerPlay;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayInKeepAlive implements PacketInPlay {

    @Override
    public void read(final String in) {
        // TODO
    }

    @Override
    public void broadcast(final PacketListenerPlay listener) throws InvalidPacketFormatException {
        listener.keepAlive(this);
    }
}

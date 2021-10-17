package jsmahy.ups_client.net.in;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayInKeepAlive implements PacketInPlay {

    @Override
    public void read(final DataInputStream in) throws IOException {
    }

    @Override
    public void broadcast(final PacketListenerPlay listener) {
        listener.keepAlive(this);
    }
}

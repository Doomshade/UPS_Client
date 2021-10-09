package jsmahy.ups_client.net.in;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayInKeepAlive implements PacketInPlay {

    private long delay;

    @Override
    public void read(final DataInputStream in) throws IOException {
        delay = in.readLong();
    }

    @Override
    public void broadcast(final PacketListenerPlay listener) {
        listener.keepAlive(this);
    }

    public long getDelay() {
        return delay;
    }
}

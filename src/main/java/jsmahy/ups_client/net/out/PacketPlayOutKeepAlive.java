package jsmahy.ups_client.net.out;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutKeepAlive implements PacketOut {
    private final long time;

    public PacketPlayOutKeepAlive(long time){
        this.time = time;
    }

    @Override
    public void write(final DataOutputStream out) throws IOException {
        out.writeLong(time);
    }

}

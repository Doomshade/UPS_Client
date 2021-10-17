package jsmahy.ups_client.net.out;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This packet is sent periodically to check whether the connection is still up
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutKeepAlive implements PacketOut {

    @Override
    public void write(final OutputStream out) throws IOException {
    }

}

package jsmahy.ups_client.net.out;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * This packet is sent periodically to check whether the connection is still up
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutKeepAlive implements PacketOut {

    @Override
    public void write(final BufferedOutputStream out) throws IOException {
    }

}

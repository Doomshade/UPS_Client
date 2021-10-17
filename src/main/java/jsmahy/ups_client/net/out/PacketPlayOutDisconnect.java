package jsmahy.ups_client.net.out;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * This packet is sent when the player disconnects via an exit button
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutDisconnect implements PacketOut {
    @Override
    public void write(final BufferedOutputStream out) throws IOException {

    }
}

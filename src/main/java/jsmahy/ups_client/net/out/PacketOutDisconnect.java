package jsmahy.ups_client.net.out;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This packet is sent when the player disconnects via an exit button
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketOutDisconnect implements PacketOut {

    private final String reason;

    public PacketOutDisconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void write(final BufferedOutputStream out) throws IOException {
        out.write(reason.getBytes(StandardCharsets.UTF_8));
    }
}

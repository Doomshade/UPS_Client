package jsmahy.ups_client.net.out.just_connected;

import jsmahy.ups_client.net.out.PacketOut;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This packet is sent when the player joins a lobby or attempts to reconnect
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketJustConnectedOutHello implements PacketOut {
    private final String playerName;

    public PacketJustConnectedOutHello(final String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void write(final BufferedOutputStream out) throws IOException {
        out.write(playerName.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("playerName", playerName)
                .toString();
    }
}

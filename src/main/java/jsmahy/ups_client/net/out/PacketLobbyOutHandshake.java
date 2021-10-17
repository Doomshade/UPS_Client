package jsmahy.ups_client.net.out;

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
public class PacketLobbyOutHandshake implements PacketOut {
    private final String playerName;

    public PacketLobbyOutHandshake(final String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void write(final BufferedOutputStream out) throws IOException {
        out.write(playerName.getBytes(StandardCharsets.UTF_8));
    }
}

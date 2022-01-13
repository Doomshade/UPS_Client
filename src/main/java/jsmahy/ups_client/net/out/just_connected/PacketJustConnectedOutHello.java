package jsmahy.ups_client.net.out.just_connected;

import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.PacketDataField;
import jsmahy.ups_client.net.out.PacketOut;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * This packet is sent when the player joins a lobby or attempts to reconnect
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketJustConnectedOutHello implements PacketOut {
    @PacketDataField
    private final String playerName;

    public PacketJustConnectedOutHello(final String playerName) {
        this.playerName = playerName;
    }

    public static Object[] deserializeParams(String data) {
        return new Object[]{data};
    }

    @Override
    public void write(final OutputStream out) throws IOException {
        try {
            Client.setLoginName(playerName);
        } catch (IllegalArgumentException e) {
            throw new IOException("Name cannot be empty!", e);
        }
        out.write(playerName.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("playerName", playerName)
                .toString();
    }
}

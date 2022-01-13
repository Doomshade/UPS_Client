package jsmahy.ups_client.net.out.just_connected;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.out.PacketOut;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This packet is sent when the player joins a lobby or attempts to reconnect
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketJustConnectedOutHello implements PacketOut {
    @PacketDataField
    private String playerName = "";

    public PacketJustConnectedOutHello(final String playerName) {
        this.playerName = playerName;
    }

    public static Object[] deserializeParams(String data) {
        return new Object[]{data};
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("playerName", playerName)
                .toString();
    }
}

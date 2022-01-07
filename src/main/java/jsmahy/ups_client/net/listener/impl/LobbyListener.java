package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.in.just_connected.packet.PacketJustConnectedInHello;
import jsmahy.ups_client.net.listener.PacketListenerJustConnected;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class LobbyListener implements PacketListenerJustConnected {

    private final NetworkManager netMan = NetworkManager.getInstance();

    public LobbyListener() {
    }

    @Override
    public void onHello(final PacketJustConnectedInHello packet) throws
            InvalidPacketFormatException {
        switch (packet.getResponseCode()) {
            case OK:
                break;
            case REJECTED:
                break;
            default:
                throw new InvalidPacketFormatException(
                        String.format("%s response code is not checked for",
                                packet.getResponseCode()));
        }
    }
}

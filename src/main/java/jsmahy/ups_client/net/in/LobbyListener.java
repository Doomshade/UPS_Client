package jsmahy.ups_client.net.in;

import jsmahy.ups_client.net.NetworkManager;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class LobbyListener implements PacketListenerLobby {

    private final NetworkManager netMan = NetworkManager.getInstance();

    public LobbyListener() {
    }

    @Override
    public void onHandshake(final PacketLobbyInHandshake packet) {
    }
}

package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.net.in.PacketLobbyInGameStart;
import jsmahy.ups_client.net.in.PacketLobbyInHandshake;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketListenerLobby extends PacketListener {
    void onHandshake(PacketLobbyInHandshake packet);

    void onGameStart(PacketLobbyInGameStart packet);
}

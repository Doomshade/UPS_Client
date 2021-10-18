package jsmahy.ups_client.net.in;

import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.listener.PacketListenerLobby;

/**
 * Incoming packets in the {@link ProtocolState#LOBBY} state.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketInLobby extends PacketIn<PacketListenerLobby> {
}

package jsmahy.ups_client.net.in.just_connected;

import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListenerJustConnected;

/**
 * Incoming packets in the {@link ProtocolState#JUST_CONNECTED} state.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketInJustConnected extends PacketIn<PacketListenerJustConnected> {
}

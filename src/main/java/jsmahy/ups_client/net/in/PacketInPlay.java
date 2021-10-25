package jsmahy.ups_client.net.in;

import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.listener.PacketListenerPlay;

/**
 * Incoming packets in the {@link ProtocolState#PLAY} state.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketInPlay extends PacketIn<PacketListenerPlay> {
}

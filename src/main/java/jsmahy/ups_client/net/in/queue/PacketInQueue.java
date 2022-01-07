package jsmahy.ups_client.net.in.queue;

import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListenerQueue;

/**
 * Incoming packets in the {@link ProtocolState#QUEUE} state.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketInQueue extends PacketIn<PacketListenerQueue> {
}

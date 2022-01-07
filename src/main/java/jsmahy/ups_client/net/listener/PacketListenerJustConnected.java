package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.just_connected.packet.PacketJustConnectedInHello;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketListenerJustConnected extends PacketListener {
    void onHello(PacketJustConnectedInHello packet) throws InvalidPacketFormatException;
}

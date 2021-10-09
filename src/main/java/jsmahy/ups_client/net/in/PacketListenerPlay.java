package jsmahy.ups_client.net.in;

import jsmahy.ups_client.net.out.PacketPlayOutDisconnect;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketListenerPlay extends PacketListener {
    void onMove(PacketPlayInMove packetPlayInMove);

    void keepAlive(PacketPlayInKeepAlive packetPlayInKeepAlive);
}

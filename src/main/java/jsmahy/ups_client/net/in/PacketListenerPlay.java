package jsmahy.ups_client.net.in;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketListenerPlay extends PacketListener {
    void onMove(PacketPlayInMove packetPlayInMove);

    void keepAlive(PacketPlayInKeepAlive packetPlayInKeepAlive);
}

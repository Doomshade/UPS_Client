package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.play.packet.PacketPlayInDrawOffer;
import jsmahy.ups_client.net.in.play.packet.PacketPlayInKeepAlive;
import jsmahy.ups_client.net.in.play.packet.PacketPlayInMove;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketListenerPlay extends PacketListener {
    void onMove(PacketPlayInMove packetPlayInMove) throws InvalidPacketFormatException;

    void keepAlive(PacketPlayInKeepAlive packetPlayInKeepAlive) throws InvalidPacketFormatException;

    void onDrawOffer(PacketPlayInDrawOffer packetPlayInDrawOffer)
            throws InvalidPacketFormatException;
}

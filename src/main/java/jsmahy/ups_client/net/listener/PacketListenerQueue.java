package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInGameStart;

public interface PacketListenerQueue extends PacketListener {
    void onGameStart(PacketQueueInGameStart packetQueueInGameStart) throws InvalidPacketFormatException;
}

package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInGameStart;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInLeaveQueue;

public interface PacketListenerQueue extends PacketListener {
    void onGameStart(PacketQueueInGameStart packetQueueInGameStart) throws InvalidPacketFormatException;

    void onLeaveQueue(PacketQueueInLeaveQueue packet) throws InvalidPacketFormatException;
}

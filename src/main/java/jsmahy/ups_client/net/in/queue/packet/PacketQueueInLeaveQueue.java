package jsmahy.ups_client.net.in.queue.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.impl.QueueListener;

public class PacketQueueInLeaveQueue implements PacketIn<QueueListener> {
    private ResponseCode rc = ResponseCode.NONE;
    @Override
    public void read(String in) throws InvalidPacketFormatException {
        try {
            rc = ResponseCode.getResponseCode(in);
        } catch (IllegalArgumentException e) {
            throw new InvalidPacketFormatException(e);
        }
    }

    public ResponseCode getResponseCode() {
        return rc;
    }

    @Override
    public void broadcast(QueueListener listener) throws InvalidPacketFormatException {
        listener.onLeaveQueue(this);
    }
}

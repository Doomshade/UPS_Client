package jsmahy.ups_client.net.in.queue.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.queue.PacketInQueue;

public class PacketQueueInLeaveQueue implements PacketInQueue {
    private ResponseCode rc = ResponseCode.NONE;

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

}

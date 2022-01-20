package jsmahy.ups_client.net.in.queue.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.queue.PacketInQueue;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketQueueInLeaveQueue implements PacketInQueue {
	@PacketDataField
	private ResponseCode rc = ResponseCode.NONE;

	public ResponseCode getResponseCode() {
		return rc;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("rc", rc)
				.toString();
	}
}

package jsmahy.ups_client.net.in.all;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.PacketIn;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketInDisconnect implements PacketIn {
	@PacketDataField
	private String reason = "";

	public String getReason() {
		return reason;
	}
}

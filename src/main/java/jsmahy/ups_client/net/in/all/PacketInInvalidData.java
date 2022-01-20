package jsmahy.ups_client.net.in.all;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.PacketIn;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketInInvalidData implements PacketIn {
	@PacketDataField
	private String invalidData = "";

	public String getInvalidData() {
		return invalidData;
	}
}

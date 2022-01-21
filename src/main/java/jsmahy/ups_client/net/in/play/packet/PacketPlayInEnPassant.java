package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayInEnPassant implements PacketInPlay {
	@PacketDataField
	private Square pawnSquare = new Square(0, 0);

	public Square getPawnSquare() {
		return pawnSquare;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("pawnSquare", pawnSquare)
				.toString();
	}
}

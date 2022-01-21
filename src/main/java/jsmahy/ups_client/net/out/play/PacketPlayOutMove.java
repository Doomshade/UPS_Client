package jsmahy.ups_client.net.out.play;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.out.PacketOut;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * This packet is sent whenever the player makes a move
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PacketPlayOutMove implements PacketOut {
	private static final Map<Integer, Pair<Square, Square>> MOVES = new HashMap<>();
	private static int MOVE_IDS = 0;

	@PacketDataField(0)
	private final int moveId = MOVE_IDS++;
	@PacketDataField(1)
	private final Square from;
	@PacketDataField(2)
	private final Square to;

	public PacketPlayOutMove(final Square from, final Square to) {
		this.from = from;
		this.to = to;
		MOVES.put(moveId, new Pair<>(from, to));
	}

	public static Pair<Square, Square> getMove(int moveId) {
		if (!MOVES.containsKey(moveId)) {
			throw new IllegalArgumentException(String.format("Move ID %s does not exist!", moveId));
		}
		return MOVES.get(moveId);
	}

	public int getMoveId() {
		return moveId;
	}

	public Square getFrom() {
		return from;
	}

	public Square getTo() {
		return to;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("from", from)
				.append("to", to)
				.toString();
	}

}

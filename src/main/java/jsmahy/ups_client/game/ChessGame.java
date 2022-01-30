package jsmahy.ups_client.game;

import jsmahy.ups_client.net.listener.impl.Client;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ChessGame {

	private static final Logger L = LogManager.getLogger(ChessGame.class);
	private final Chessboard chessboard;
	private ChessPlayer opponent = new ChessPlayer("Unknown");
	private boolean hasOpponent = false;

	/**
	 * Instantiates a new Chess game.
	 *
	 * @param chessboard the chessboard
	 */
	public ChessGame(Chessboard chessboard) {
		this.chessboard = chessboard;
		L.debug("Instantiated a new game " + chessboard);
	}

	public Chessboard getChessboard() {
		return chessboard;
	}

	public ChessPlayer getOpponent() throws IllegalStateException {
		if (!hasOpponent()) {
			throw new IllegalStateException("Opponent not yet set in this game! " + this);
		}
		return opponent;
	}

	/**
	 * Sets the opponent
	 *
	 * @param opponent the opponent
	 *
	 * @throws IllegalStateException if the opponent has already been set
	 */
	public void setOpponent(ChessPlayer opponent) throws IllegalStateException {
		if (hasOpponent()) {
			throw new IllegalStateException("Opponent already set in this game! " + this);
		}
		this.opponent = opponent;
		this.opponent.setColour(!Client.getClient().getPlayer().isWhite());
		this.hasOpponent = true;
		L.info("Set opponent to " + opponent);
		L.info("Current game state: " + this);
	}

	public boolean hasOpponent() {
		return hasOpponent;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("chessboard", chessboard)
				.append("opponent", opponent)
				.toString();
	}
}

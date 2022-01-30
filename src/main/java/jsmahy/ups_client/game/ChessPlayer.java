package jsmahy.ups_client.game;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class ChessPlayer {
	private static final Logger L = LogManager.getLogger(ChessPlayer.class);
	private final String name;
	private boolean hasColour = false;
	private boolean white = true;

	public ChessPlayer(final String name) {
		this.name = name;
	}

	/**
	 * Sets the colour of the player
	 *
	 * @param white whether the player is white
	 */
	public void setColour(boolean white) throws IllegalStateException {
		this.hasColour = true;
		this.white = white;
		L.debug(String.format("Set %s's colour to %s", name, white ? "white" : "black"));
	}

	/**
	 * @return {@code true} if the player is white, {@code false} otherwise
	 *
	 * @throws IllegalStateException if the player's colour has not yet been set
	 */
	public boolean isWhite() throws IllegalStateException {
		if (!hasColour) {
			throw new IllegalStateException(
					String.format("The chess player %s has no colour set!", name));
		}
		return white;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("hasColour", hasColour)
				.append("white", white)
				.toString();
	}
}

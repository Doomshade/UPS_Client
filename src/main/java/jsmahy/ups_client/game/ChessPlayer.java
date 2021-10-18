package jsmahy.ups_client.game;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class ChessPlayer {
    private final String name;
    private boolean hasColour = false;
    private boolean white = true;

    public ChessPlayer(final String name) {
        this.name = name;
    }

    /**
     * @param white
     *
     * @throws IllegalStateException
     */
    public void setColour(boolean white) throws IllegalStateException {
        if (hasColour) {
            throw new IllegalStateException("Already set up the colour!");
        }
        this.hasColour = true;
        this.white = white;
    }

    /**
     * @return
     *
     * @throws IllegalStateException
     */
    public boolean isWhite() throws IllegalStateException {
        if (!hasColour) {
            throw new IllegalStateException("The chess player %s has no colour set!");
        }
        return white;
    }

    public String getName() {
        return name;
    }
}

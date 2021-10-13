package jsmahy.ups_client.game;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class ChessPlayer {
    private final boolean white;
    private final String name;

    public ChessPlayer(final String name, final boolean white) {
        this.white = white;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isWhite() {
        return white;
    }
}

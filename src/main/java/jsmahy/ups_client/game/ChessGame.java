package jsmahy.ups_client.game;

import jsmahy.ups_client.net.Player;
import jsmahy.ups_client.util.Position;

/**
 * The type Chess game.
 */
public final class ChessGame {
    private final Chessboard chessboard;
    private final Player white;
    private final Player black;

    /**
     * Instantiates a new Chess game.
     *
     * @param chessboard the chessboard
     * @param white      the white
     * @param black      the black
     */
    public ChessGame(Chessboard chessboard, Player white, Player black) {
        this.chessboard = chessboard;
        this.white = white;
        this.black = black;
    }

    /**
     * Gets chessboard.
     *
     * @return the chessboard
     */
    public Chessboard getChessboard() {
        return chessboard;
    }

    /**
     * Gets white.
     *
     * @return the white
     */
    public Player getWhite() {
        return white;
    }

    /**
     * Gets black.
     *
     * @return the black
     */
    public Player getBlack() {
        return black;
    }

    /**
     * Move piece.
     *
     * @param from the from
     * @param to   the to
     */
    public void movePiece(Position from, Position to) {
        if (chessboard.move(from, to)) {
            // send packet to players
        }
    }
}

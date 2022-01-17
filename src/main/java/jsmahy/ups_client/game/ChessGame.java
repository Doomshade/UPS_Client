package jsmahy.ups_client.game;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public final class ChessGame {

    private final List<ChessMove> moveList = new ArrayList<>();
    private static final Logger L = LogManager.getLogger(ChessGame.class);
    private final Chessboard chessboard;
    private ChessPlayer opponent = new ChessPlayer("Unknown");
    private boolean hasOpponent = false;

    private boolean clientToMove = true;

    /**
     * Instantiates a new Chess game.
     *
     * @param chessboard the chessboard
     */
    public ChessGame(Chessboard chessboard) {
        this.chessboard = chessboard;
        this.clientToMove = Client.getClient().getPlayer().isWhite();
        L.debug("Instantiated a new game " + chessboard);
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public boolean isClientToMove() {
        return clientToMove;
    }

    public ChessPlayer getOpponent() throws IllegalStateException {
        if (!hasOpponent()) {
            throw new IllegalStateException("Opponent not yet set in this game! " + this);
        }
        return opponent;
    }

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

    public void nextTurn() {
        this.clientToMove = !this.clientToMove;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("chessboard", chessboard)
                .append("opponent", opponent)
                .append("clientToMove", clientToMove)
                .toString();
    }
}

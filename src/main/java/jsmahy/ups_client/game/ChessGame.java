package jsmahy.ups_client.game;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ChessGame {

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

    /**
     * Moves a piece and propagates the move to the server if source of the movement was the client
     *
     * @param from the from
     * @param to   the to
     */
    public void movePiece(Square from, Square to) {
        // the move to perform as
        final ChessPlayer as = clientToMove ? Client.getClient().getPlayer() : getOpponent();
        if (chessboard.move(from, to, as) != ChessMove.NO_MOVE && clientToMove) {
            // send packet to players
            NetworkManager.getInstance().sendPacket(new PacketPlayOutMove(from, to));
        }
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

package jsmahy.ups_client.game;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.PlayListener;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.Square;

public final class ChessGame {
    private final Chessboard chessboard;
    private final PlayListener client;
    private final ChessPlayer opponent;

    private boolean clientToMove = true;

    /**
     * Instantiates a new Chess game.
     *
     * @param chessboard the chessboard
     * @param client     the player
     * @param opponent   the opponent
     */
    public ChessGame(Chessboard chessboard, PlayListener client, ChessPlayer opponent,
                     boolean clientIsWhite) {
        this.chessboard = chessboard;
        this.client = client;
        this.opponent = opponent;
        this.client.getPlayer().setColour(clientIsWhite);
        opponent.setColour(!clientIsWhite);
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
        final ChessPlayer as = clientToMove ? getClient().getPlayer() : getOpponent();
        if (chessboard.move(from, to, as) != ChessMove.NO_MOVE && clientToMove) {
            // send packet to players
            NetworkManager.getInstance().sendPacket(new PacketPlayOutMove(from, to));
        }
    }

    public PlayListener getClient() {
        return client;
    }

    public ChessPlayer getOpponent() {
        return opponent;
    }

    public void nextTurn() {
        this.clientToMove = !this.clientToMove;
    }
}

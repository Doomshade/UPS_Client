package jsmahy.ups_client.game;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.PlayerConnection;
import jsmahy.ups_client.net.out.PacketPlayOutMove;
import jsmahy.ups_client.util.Position;

public final class ChessGame {
    private static ChessGame chessGame = null;
    private final Chessboard chessboard;
    private final PlayerConnection client;
    private final ChessPlayer opponent;

    private boolean clientToMove = true;

    /**
     * Instantiates a new Chess game.
     *
     * @param chessboard the chessboard
     * @param client     the player
     * @param opponent   the opponent
     */
    public ChessGame(Chessboard chessboard, PlayerConnection client, ChessPlayer opponent,
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
     * Moves a piece and propagates the move to the server.
     *
     * @param from the from
     * @param to   the to
     */
    public void movePiece(Position from, Position to) {
        // the move to perform as
        final ChessPlayer as = clientToMove ? getClient().getPlayer() : getOpponent();
        if (chessboard.move(from, to, as) != ChessMove.NO_MOVE && clientToMove) {
            // send packet to players
            NetworkManager.getInstance().sendPacket(new PacketPlayOutMove(from, to));
        }
    }

    public PlayerConnection getClient() {
        return client;
    }

    public ChessPlayer getOpponent() {
        return opponent;
    }

    public void nextTurn() {
        this.clientToMove = !this.clientToMove;
    }
}

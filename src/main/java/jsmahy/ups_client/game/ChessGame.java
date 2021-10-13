package jsmahy.ups_client.game;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.in.PlayerConnection;
import jsmahy.ups_client.net.out.PacketPlayOutMove;
import jsmahy.ups_client.util.Position;

import java.util.regex.Matcher;

public final class ChessGame {
    private static ChessGame chessGame = null;
    private final Chessboard chessboard;
    private final PlayerConnection client;
    private final ChessPlayer opponent;

    private boolean clientToMove = true;

    public static void setupChessGame(Chessboard chessboard, PlayerConnection client,
                                      ChessPlayer opponent) {
        if (isSetUp()) {
            throw new IllegalStateException("Chess game has already been set up!");
        }
        chessGame = new ChessGame(chessboard, client, opponent);
    }

    public static boolean isSetUp() {
        return chessGame != null;
    }

    public static ChessGame getChessGame() {
        return chessGame;
    }

    /**
     * Instantiates a new Chess game.
     *
     * @param chessboard the chessboard
     * @param client     the player
     * @param opponent   the opponent
     */
    private ChessGame(Chessboard chessboard, PlayerConnection client, ChessPlayer opponent) {
        this.chessboard = chessboard;
        this.client = client;
        this.opponent = opponent;
        client.startGame(this, true);
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public PlayerConnection getClient() {
        return client;
    }

    public boolean isClientToMove() {
        return clientToMove;
    }

    public ChessPlayer getOpponent() {
        return opponent;
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

    public void nextTurn() {
        this.clientToMove = !this.clientToMove;
    }
}

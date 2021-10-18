package jsmahy.ups_client.net.in;

import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class LobbyListener implements PacketListenerLobby {

    private final NetworkManager netMan = NetworkManager.getInstance();

    public LobbyListener() {
    }

    @Override
    public void onHandshake(final PacketLobbyInHandshake packet) {
        switch (packet.getResponseCode()) {
            case OK:
                break;
            case REJECTED:
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("%s response code is not checked for",
                                packet.getResponseCode()));
        }
    }

    @Override
    public void onGameStart(final PacketLobbyInGameStart packet) {
        if (packet.getResponseCode() != ResponseCode.CONNECT) {
            return;
        }
        // set up the board from the fen string
        final String fen = packet.getFenString();
        if (fen.isEmpty()) {
            throw new IllegalStateException("The server sent a reconnect packet, but did " +
                    "not send a FEN string to update the board!");
        }
        final Chessboard chessboard = new Chessboard();
        chessboard.setupBoard(fen);

        // set up the game and change the state
        final PlayerConnection player =
                new PlayerConnection(new ChessPlayer("Testshade"));
        final ChessPlayer opponent = new ChessPlayer(packet.getOpponentName());
        ChessGame game = new ChessGame(chessboard, player, opponent, packet.isWhite());
        player.startGame(game);
    }
}

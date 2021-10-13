package jsmahy.ups_client.net.in;

import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;

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
        switch (packet.getResponseCode()){
            case OK:
                break;
            case REJECTED:
                break;
            case RECONNECT:
                final String fen = packet.getFenString();
                if (fen.isEmpty()){
                    throw new IllegalStateException("The server sent a reconnect packet, but did " +
                            "not send a FEN string to update the board!");
                }
                PlayerConnection player =
                        new PlayerConnection(new ChessPlayer("Testshade", packet.isWhite()));
                ChessPlayer opponent = new ChessPlayer(packet.getOpponentName(), !packet.isWhite());
                Chessboard chessboard = new Chessboard();
                chessboard.setupBoard(fen);
                ChessGame.setupChessGame(chessboard, player, opponent);
                netMan.changeState(ProtocolState.PLAY);
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("%s response code is not checked for",
                                packet.getResponseCode()));
        }
    }
}

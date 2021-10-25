package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.exception.InvalidFENFormatException;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.PacketLobbyInGameStart;
import jsmahy.ups_client.net.in.PacketLobbyInHandshake;

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
    public void onHandshake(final PacketLobbyInHandshake packet) throws
            InvalidPacketFormatException {
        switch (packet.getResponseCode()) {
            case OK:
                break;
            case REJECTED:
                break;
            default:
                throw new InvalidPacketFormatException(
                        String.format("%s response code is not checked for",
                                packet.getResponseCode()));
        }
    }

    @Override
    public void onGameStart(final PacketLobbyInGameStart packet)
            throws InvalidPacketFormatException {
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
        try {
            chessboard.setupBoard(fen);
        } catch (InvalidFENFormatException e) {
            throw new InvalidPacketFormatException(e);
        }

        // set up the game and change the state
        final PlayerConnection player =
                new PlayerConnection(new ChessPlayer("Testshade"));
        final ChessPlayer opponent = new ChessPlayer(packet.getOpponentName());
        ChessGame game = new ChessGame(chessboard, player, opponent, packet.isWhite());
        player.startGame(game);
    }
}

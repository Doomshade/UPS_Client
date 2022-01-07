package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.exception.InvalidFENFormatException;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInGameStart;
import jsmahy.ups_client.net.listener.PacketListenerQueue;

public class QueueListener implements PacketListenerQueue {
    @Override
    public void onGameStart(PacketQueueInGameStart packet) throws InvalidPacketFormatException {
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

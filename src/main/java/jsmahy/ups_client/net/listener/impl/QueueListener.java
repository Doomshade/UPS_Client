package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.exception.InvalidFENFormatException;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInGameStart;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInLeaveQueue;

public class QueueListener extends AbstractListener {

    {
        register(PacketQueueInGameStart.class, this::onGameStart);
        register(PacketQueueInLeaveQueue.class, this::onLeaveQueue);
    }

    private void onGameStart(PacketQueueInGameStart packet) throws InvalidPacketFormatException {
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
        Client c = Client.getClient();
        c.getPlayer().setColour(packet.isWhite());
        ChessGame game = new ChessGame(chessboard);
        c.startGame(game);
        NetworkManager.getInstance().changeState(ProtocolState.PLAY);
    }

    private void onLeaveQueue(PacketQueueInLeaveQueue packet) throws InvalidPacketFormatException {
        switch (packet.getResponseCode()) {
            case OK:
                NetworkManager.getInstance().changeState(ProtocolState.LOGGED_IN);
                break;
            case REJECTED:
                // TODO
                throw new IllegalStateException("Could not leave the queue!");
            default:
                throw new InvalidPacketFormatException("Invalid response code received!");
        }
    }
}

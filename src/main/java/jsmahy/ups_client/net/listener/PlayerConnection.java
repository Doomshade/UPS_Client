package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.PacketListenerPlay;
import jsmahy.ups_client.net.in.PacketPlayInDrawOffer;
import jsmahy.ups_client.net.in.PacketPlayInKeepAlive;
import jsmahy.ups_client.net.in.PacketPlayInMove;
import jsmahy.ups_client.net.out.PacketOutDisconnect;
import jsmahy.ups_client.net.out.PacketPlayOutKeepAlive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerConnection implements PacketListenerPlay {
    public static final int SERVER_RESPONSE_LIMIT = 25_000;
    public static final int KEEPALIVE_CHECK_PERIOD = 1_000;
    private static final Logger L = LogManager.getLogger(PlayerConnection.class);
    private static final NetworkManager NET_MAN = NetworkManager.getInstance();
    // TODO rename
    private static final int DRAW_OFFER_MAX_DELAY = 15_000;
    private final ChessPlayer player;
    private boolean awaitingKeepAlive = false;
    private long keepAlive = System.currentTimeMillis();
    private ChessGame chessGame = null;
    private long timeSinceLastDrawOffer = 0L;

    public PlayerConnection(ChessPlayer player) {
        this.player = player;
        NetworkManager.setPlayListener(this);
    }

    public void startGame(ChessGame chessGame) {
        if (this.chessGame != null) {
            throw new IllegalStateException("A chess game is already in play!");
        }
        this.chessGame = chessGame;
        NET_MAN.changeState(ProtocolState.PLAY);
        //startKeepAlive();
    }

    private void startKeepAlive() {
        final TimerTask keepAlive = new TimerTask() {
            @Override
            public void run() {
                long currKeepAlive = System.currentTimeMillis() - PlayerConnection.this.keepAlive;
                // check if the server is still alive
                if (currKeepAlive >= SERVER_RESPONSE_LIMIT) {
                    if (awaitingKeepAlive) {
                        disconnect("Have not received keepAlive packet in a while");
                    } else {
                        awaitingKeepAlive = true;
                        PlayerConnection.this.keepAlive = System.currentTimeMillis();
                    }
                }

                PlayerConnection.this.keepAlive = System.currentTimeMillis();
                NET_MAN.sendPacket(new PacketPlayOutKeepAlive());

            }
        };
        Timer timer = new Timer("keepAlive", true);
        timer.schedule(keepAlive, 0, KEEPALIVE_CHECK_PERIOD);
    }

    public void disconnect(String reason) {
        L.info("Disconnecting from the server...");
        NET_MAN.sendPacket(new PacketOutDisconnect(reason));
        try {
            NET_MAN.stopListening();
        } catch (IOException e) {
            L.error("An exception occurred when trying to close the socket", e);
        }
    }

    public ChessPlayer getPlayer() {
        return player;
    }

    @Override
    public void onMove(final PacketPlayInMove packetPlayInMove) {
        switch (packetPlayInMove.getResponseCode()) {
            case REJECTED:
                // revert the move
                // the server will flip the positions for us
            case MOVE:
                // move the piece
                chessGame.movePiece(packetPlayInMove.getFrom(), packetPlayInMove.getTo());
                chessGame.nextTurn();
                break;
            case OK:
                // don't do anything
                break;
        }
    }

    @Override
    public void keepAlive(final PacketPlayInKeepAlive packetPlayInKeepAlive) {
        awaitingKeepAlive = false;
    }

    @Override
    public void onDrawOffer(final PacketPlayInDrawOffer packetPlayInDrawOffer) {
        L.trace("Received a draw offer");
        final long millis = System.currentTimeMillis();
        if (millis - timeSinceLastDrawOffer > DRAW_OFFER_MAX_DELAY) {
            timeSinceLastDrawOffer = millis;
            L.debug("Showing draw offer available responses...");
        }
    }
}

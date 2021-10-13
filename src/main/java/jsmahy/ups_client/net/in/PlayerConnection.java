package jsmahy.ups_client.net.in;

import jsmahy.ups_client.HelloApplication;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.out.PacketPlayOutDisconnect;
import jsmahy.ups_client.net.out.PacketPlayOutKeepAlive;
import jsmahy.ups_client.util.Position;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerConnection implements PacketListenerPlay {
    private static final Logger L = HelloApplication.getLogger();
    public static final int SERVER_RESPONSE_LIMIT = 25_000;
    public static final int KEEPALIVE_CHECK_PERIOD = 1_000;
    private static final NetworkManager NET_MAN = NetworkManager.getInstance();

    private final ChessPlayer player;
    private boolean awaitingKeepAlive = false;
    private long keepAlive = System.currentTimeMillis();
    private ChessGame chessGame = null;

    public PlayerConnection(ChessPlayer player) {
        this.player = player;
        NetworkManager.setPlayListener(this);
    }

    public void startGame(final ChessGame chessGame, boolean white) {
        this.chessGame = chessGame;
        startKeepAlive();
    }

    private void startKeepAlive() {
        final TimerTask keepAlive = new TimerTask() {
            @Override
            public void run() {
                long currKeepAlive = System.currentTimeMillis() - PlayerConnection.this.keepAlive;
                // check if the server is still alive
                if (currKeepAlive >= SERVER_RESPONSE_LIMIT) {
                    if (awaitingKeepAlive) {
                        disconnect();
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

    public void disconnect() {
        NET_MAN.sendPacket(new PacketPlayOutDisconnect());
        try {
            NET_MAN.stopListening();
        } catch (IOException e) {
            L.error("An exception occurred when trying to close the socket", e);
        }
    }

    @Override
    public void onMove(final PacketPlayInMove packetPlayInMove) {
        switch (packetPlayInMove.getResponseCode()) {
            case REJECTED:
                // revert the move
                // the server will flip the positions for us
            case MOVE:
                // move the piece

                break;
            case OK:
                // don't do anything
                break;
        }
    }

    public void movePiece(Position from, Position to) {
        // TODO
    }

    @Override
    public void keepAlive(final PacketPlayInKeepAlive packetPlayInKeepAlive) {
        awaitingKeepAlive = false;
    }
}

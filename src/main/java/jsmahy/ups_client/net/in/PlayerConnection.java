package jsmahy.ups_client.net.in;

import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.out.PacketPlayOutDisconnect;
import jsmahy.ups_client.net.out.PacketPlayOutKeepAlive;
import jsmahy.ups_client.util.Position;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerConnection implements PacketListenerPlay {
    public static final int SERVER_RESPONSE_LIMIT = 25_000;
    public static final int KEEPALIVE_CHECK_PERIOD = 1_000;
    private static final NetworkManager NET_MAN = NetworkManager.getInstance();

    private final ChessPlayer player;
    private boolean awaitingKeepAlive = false;
    private long keepAlive = System.currentTimeMillis();
    private ChessGame chessGame = null;

    public PlayerConnection(ChessPlayer player) {
        this.player = player;
        NetworkManager.setListener(this);
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
                NET_MAN.sendPacket(new PacketPlayOutKeepAlive(PlayerConnection.this.keepAlive));

            }
        };
        Timer timer = new Timer("keepAlive", true);
        timer.schedule(keepAlive, 0, KEEPALIVE_CHECK_PERIOD);
    }

    public void disconnect() {
        NET_MAN.sendPacket(new PacketPlayOutDisconnect());
        NET_MAN.stopListening();
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
        if (awaitingKeepAlive && packetPlayInKeepAlive.getDelay() == keepAlive) {
            awaitingKeepAlive = false;
        }
    }
}

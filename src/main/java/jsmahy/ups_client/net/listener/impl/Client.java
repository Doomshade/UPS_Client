package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessMove;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.play.packet.*;
import jsmahy.ups_client.net.out.play.PacketPlayOutKeepAlive;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Client extends AbstractListener {
    public static final int SERVER_RESPONSE_LIMIT = 25_000;
    public static final int KEEPALIVE_CHECK_PERIOD = 1_000;
    private static final Logger L = LogManager.getLogger(Client.class);
    // TODO rename
    private static final int DRAW_OFFER_MAX_DELAY = 15_000;
    private static Client instance = null;
    private static String name = "";
    private final ChessPlayer player;
    private final Map<Integer, PacketPlayOutMove> lastMoves = new HashMap<>();
    private boolean awaitingKeepAlive = false;
    private long keepAlive = System.currentTimeMillis();
    private ChessGame chessGame = null;
    private long timeSinceLastDrawOffer = 0L;
    private boolean receivedMoveResponse = false;

    {
        register(PacketPlayInMove.class, this::onMove);
        register(PacketPlayInKeepAlive.class, this::keepAlive);
        register(PacketPlayInOpponentName.class, this::onOpponentName);
        register(PacketPlayInMessage.class, this::onMessage);
        register(PacketPlayInDrawOffer.class, this::onDrawOffer);
        register(PacketPlayInGameFinish.class, this::onGameFinish);
        register(PacketPlayInMoveResponse.class, this::onMoveResponse);
        register(PacketPlayInCastles.class, this::onCastles);

        this.player = new ChessPlayer(name);
        NetworkManager.setClient(this);
        L.info("Logged in as " + this);
    }

    public static Client getClient() {
        if (instance == null) {
            throw new IllegalStateException("Not yet logged in!");
        }
        return instance;
    }

    public static void setLoginName(String name) throws NullPointerException, IllegalArgumentException {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        Client.name = name;
    }

    public static void login() {
        if (instance != null) {
            throw new IllegalStateException("Already logged in as " + instance.getPlayer().getName());
        }
        if (name.isEmpty()) {
            throw new IllegalStateException("Haven't set the login name yet!");
        }
        L.info(String.format("Logging in as %s...", name));
        instance = new Client();
    }

    private void onCastles(PacketPlayInCastles packet) {

    }

    private void onMoveResponse(PacketPlayInMoveResponse packet) {
        receivedMoveResponse = true;
        switch (packet.getResponseCode()) {
            case OK:
                break;
            case REJECTED:
                // TODO uncomment once we stop debugging via external file
                /*if (!lastMoves.containsKey(packet.getMoveId())) {
                    throw new IllegalStateException("Received a move response with an invalid ID!");
                }
                PacketPlayOutMove move = lastMoves.get(packet.getMoveId());
                chessGame.getChessboard().moveOnBoard(move.getTo(), move.getFrom());*/
                break;
            default:
                throw new IllegalStateException("Invalid response received!");
        }
    }

    private void onGameFinish(PacketPlayInGameFinish packet) {
        NetworkManager.getInstance().changeState(ProtocolState.LOGGED_IN);
    }

    public void startGame(ChessGame chessGame) {
        if (this.chessGame != null) {
            throw new IllegalStateException("A chess game is already in play!");
        }
        this.chessGame = chessGame;
        L.info("Started a new game: " + chessGame);
        //startKeepAlive();
    }

    /**
     * Attempts to move a piece on the board. Firstly it validates the move client-side,
     * then it sends a packet to the server for confirmation.
     *
     * @param from
     * @param to
     * @return {@code true} if the move was successful, false otherwise
     */
    public boolean movePiece(Square from, Square to) {
        // first check if it's our turn
        if (!chessGame.isClientToMove()) {
            return false;
        }

        // the server hasn't responded yet, don't do anything
        if (!receivedMoveResponse) {
            return false;
        }

        // then attempt to move the piece client side
        if (chessGame.getChessboard().move(from, to, this.player) != ChessMove.NO_MOVE) {
            // the move is likely valid, send it to the server and wait for response
            PacketPlayOutMove moveOut = new PacketPlayOutMove(from, to);
            if (lastMoves.containsKey(moveOut.getMoveId())) {
                throw new IllegalStateException("Attempted to send a move packet with the same ID twice!");
            }
            NetworkManager.getInstance().sendPacket(moveOut);
            lastMoves.put(moveOut.getMoveId(), moveOut);
            receivedMoveResponse = false;
            return true;
        }
        return false;
    }

    private void startKeepAlive() {
        final TimerTask keepAlive = new TimerTask() {
            @Override
            public void run() {
                long currKeepAlive = System.currentTimeMillis() - Client.this.keepAlive;
                // check if the server is still alive
                if (currKeepAlive >= SERVER_RESPONSE_LIMIT) {
                    if (awaitingKeepAlive) {
                        disconnect("Have not received keepAlive packet in a while");
                    } else {
                        awaitingKeepAlive = true;
                        Client.this.keepAlive = System.currentTimeMillis();
                    }
                }

                Client.this.keepAlive = System.currentTimeMillis();
                NetworkManager.getInstance().sendPacket(new PacketPlayOutKeepAlive());

            }
        };
        Timer timer = new Timer("keepAlive", true);
        timer.schedule(keepAlive, 0, KEEPALIVE_CHECK_PERIOD);
    }

    public void disconnect(String reason) {
        L.info("Disconnecting from the server...");
        try {
            NetworkManager.getInstance().stopListening();
        } catch (IOException e) {
            L.error("An exception occurred when trying to close the socket", e);
        }
    }

    public ChessPlayer getPlayer() {
        return player;
    }

    private void onMove(final PacketPlayInMove packet)
            throws InvalidPacketFormatException {
        chessGame.movePiece(packet.getFrom(), packet.getTo());
        chessGame.nextTurn();
        /*switch (packet.getResponseCode()) {
            case REJECTED:
                // revert the move
                // the server will flip the positions for us
            case MOVE:
                // move the piece
                break;
            case OK:
                // don't do anything
                break;
            default:
                throw new InvalidPacketFormatException("Invalid response received");
        }*/
    }

    private void keepAlive(final PacketPlayInKeepAlive packetPlayInKeepAlive) {
        awaitingKeepAlive = false;
    }

    private void onDrawOffer(final PacketPlayInDrawOffer packetPlayInDrawOffer) {
        L.trace("Received a draw offer");
        final long millis = System.currentTimeMillis();
        if (millis - timeSinceLastDrawOffer > DRAW_OFFER_MAX_DELAY) {
            timeSinceLastDrawOffer = millis;
            L.debug("Showing draw offer available responses...");
        }
    }

    private void onMessage(PacketPlayInMessage packet) {
        L.info("Message recvd: " + packet.getMessage());
    }

    private void onOpponentName(PacketPlayInOpponentName packet) {
        chessGame.setOpponent(new ChessPlayer(packet.getOpponentName()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("player", player)
                .append("awaitingKeepAlive", awaitingKeepAlive)
                .append("keepAlive", keepAlive)
                .append("chessGame", chessGame)
                .append("timeSinceLastDrawOffer", timeSinceLastDrawOffer)
                .toString();
    }
}

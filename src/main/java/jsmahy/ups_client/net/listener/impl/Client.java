package jsmahy.ups_client.net.listener.impl;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.controller.GameController;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.play.packet.*;
import jsmahy.ups_client.net.out.play.PacketPlayOutKeepAlive;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.AlertBuilder;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private boolean awaitingKeepAlive = false;
	private long keepAlive = System.currentTimeMillis();
	private ChessGame chessGame = null;
	private long timeSinceLastDrawOffer = 0L;

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

	public static void setLoginName(String name) throws NullPointerException, IllegalArgumentException {
		if (name == null) {
			throw new NullPointerException("Name cannot be null");
		}
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty!");
		}
		Client.name = name;
	}

	public static void logout() {
		instance = null;
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

	public ChessPlayer getPlayer() {
		return player;
	}

	private void onCastles(PacketPlayInCastles packet) {

	}

	private void onMoveResponse(PacketPlayInMoveResponse packet) {
		switch (packet.getResponseCode()) {
			case OK:
				break;
			case REJECTED:
				break;
			default:
				throw new IllegalStateException("Invalid response received!");
		}
		GameController.getInstance().draggableGrid.update();
	}

	/**
	 * Attempts to move a piece on the board. Firstly it validates the move client-side, then it sends a packet to the
	 * server for confirmation.
	 *
	 * @param from
	 * @param to
	 *
	 * @return {@code true} if the move was successful, false otherwise
	 */
	private void movePiece(Square from, Square to) {
		chessGame.getChessboard().moveOnBoard(from, to);
		//chessGame.nextTurn();
		// TODO make this better
		GameController.getInstance().draggableGrid.update();
	}

	public static Client getClient() {
		if (instance == null) {
			throw new IllegalStateException("Not yet logged in!");
		}
		return instance;
	}

	private void onGameFinish(PacketPlayInGameFinish packet) {
		Platform.runLater(() -> {

			AlertBuilder ab = new AlertBuilder(Alert.AlertType.INFORMATION)
					.title("Game finished!");

			switch (packet.getFinishType()) {
				case 0:
					ab.header("Draw");
					ab.content("Game ended in a draw.");
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
			}
			ab.build().show();
		});
		NetworkManager.getInstance().changeState(ProtocolState.LOGGED_IN);
		SceneManager.changeScene(SceneManager.Scenes.PLAY_SCENE);
		chessGame = null;
	}

	public void startGame(ChessGame chessGame) {
		if (this.chessGame != null) {
			throw new IllegalStateException("A chess game is already in play!");
		}
		this.chessGame = chessGame;
		L.info("Started a new game: " + chessGame);
		//startKeepAlive();
	}

	public ChessGame getChessGame() {
		return chessGame;
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
		NetworkManager.getInstance().stopListening();
	}

	private void onMove(final PacketPlayInMove packet)
			throws InvalidPacketFormatException {
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();
		final Square from = packet.getFrom();
		final Square to = packet.getTo();
		movePiece(clientWhite ? from : from.flip(), clientWhite ? to : to.flip());
	}

	public void move(Square from, Square to) {
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();
		from = clientWhite ? from : from.flip();
		to = clientWhite ? to : to.flip();
		final PacketPlayOutMove packet = new PacketPlayOutMove(from, to);
		NetworkManager.getInstance().sendPacket(packet);
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
		final ChessPlayer opponent;
		try {
			opponent = chessGame.getOpponent();
		} catch (IllegalStateException e) {
			L.error("Failed to get the game opponent. Reason:", e);
			return;
		}
		GameController.getInstance().opponentChat.appendText(
				String.format("[%s]:\t%s%n", opponent.getName(), packet.getMessage()));
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

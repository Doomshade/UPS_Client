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
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.AlertBuilder;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client extends AbstractListener {
	private static final Logger L = LogManager.getLogger(Client.class);
	// TODO rename
	private static final int DRAW_OFFER_MAX_DELAY = 15_000;
	private static Client instance = null;
	private static String name = "";
	private final ChessPlayer player;
	private ChessGame chessGame = null;
	private long timeSinceLastDrawOffer = 0L;

	{
		register(PacketPlayInMove.class, this::onMove);
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
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();

		boolean whiteCastles = packet.isWhite();
		boolean longCastles = packet.isLongCastles();

		int rank = whiteCastles ? 0 : 7;

		int rookFromFile = longCastles ? 0 : 7;
		int rookToFile = longCastles ? 3 : 5;

		int kingFromFile = 4;
		int kingToFile = longCastles ? 2 : 6;

		L.info(String.format("white, long: %s, %s", whiteCastles, longCastles));

		moveCastles(clientWhite, rank, kingFromFile, kingToFile, "kingFrom - kingTo: %s - %s");

		moveCastles(clientWhite, rank, rookFromFile, rookToFile, "rookFrom - rookTo: %s - %s");
	}

	private void moveCastles(final boolean clientWhite, final int rank, final int fromFile, final int toFile,
	                         final String s) {
		final Square kingFrom = new Square(rank, fromFile);
		final Square kingTo = new Square(rank, toFile);

		L.info(String.format(s, kingFrom, kingTo));
		movePiece(clientWhite ? kingFrom : kingFrom.flip(), clientWhite ? kingTo : kingTo.flip());
	}

	private void onMoveResponse(PacketPlayInMoveResponse packet) {
		switch (packet.getResponseCode()) {
			case OK:
				break;
			case REJECTED:
				L.info("Attempted to play an invalid move! Move: " + PacketPlayOutMove.getMove(packet.getMoveId()));
				break;
			default:
				throw new IllegalStateException("Invalid response received!");
		}
		GameController.getInstance().draggableGrid.update();
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

	private void onMove(final PacketPlayInMove packet)
			throws InvalidPacketFormatException {
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();
		final Square from = packet.getFrom();
		final Square to = packet.getTo();
		movePiece(clientWhite ? from : from.flip(), clientWhite ? to : to.flip());
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

	public void move(Square from, Square to) {
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();
		from = clientWhite ? from : from.flip();
		to = clientWhite ? to : to.flip();
		final PacketPlayOutMove packet = new PacketPlayOutMove(from, to);
		NetworkManager.getInstance().sendPacket(packet);
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
				.append("chessGame", chessGame)
				.append("timeSinceLastDrawOffer", timeSinceLastDrawOffer)
				.toString();
	}
}

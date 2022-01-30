package jsmahy.ups_client.net.listener.impl;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.controller.GameController;
import jsmahy.ups_client.controller.ServerConnectionController;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.play.packet.*;
import jsmahy.ups_client.net.out.just_connected.PacketJustConnectedOutHello;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.AlertBuilder;
import jsmahy.ups_client.util.Square;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The listener for the {@link ProtocolState#PLAY} state
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class Client extends AbstractListener {
	private static final Logger L = LogManager.getLogger(Client.class);

	// some game related variables
	private static final int DRAW_OFFER_MAX_DELAY = 15_000;
	private static final int WIN_WHITE = 1 << 0;
	private static final int WIN_BY_MATE = 1 << 1;
	private static final int WIN_BY_RESIGN = 1 << 2;
	private static final int WIN_BY_TIME = 1 << 3;
	private static Client instance = null;

	// the name nad player instance of the client
	private static String name = "";
	private final ChessPlayer player;

	// the chess game the client is in
	private ChessGame chessGame = null;
	private long timeSinceLastDrawOffer = 0L;
	private boolean onTurn = false;

	{
		// registers packet handlers
		register(PacketPlayInMove.class, this::onMove);
		register(PacketPlayInOpponentName.class, this::onOpponentName);
		register(PacketPlayInMessage.class, this::onMessage);
		register(PacketPlayInDrawOffer.class, this::onDrawOffer);
		register(PacketPlayInGameFinish.class, this::onGameFinish);
		register(PacketPlayInMoveResponse.class, this::onMoveResponse);
		register(PacketPlayInCastles.class, this::onCastles);
		register(PacketPlayInOpponentDisconnect.class, this::onOpponentDisconnect);
		register(PacketPlayInEnPassant.class, this::onEnPassant);

		// and log in once an instance is created
		this.player = new ChessPlayer(name);
		NetworkManager.setClient(this);
		L.info("Logged in as " + this);
	}

	/**
	 * Sets the login name
	 *
	 * @param name the login name to set
	 *
	 * @throws NullPointerException     if the name is null
	 * @throws IllegalArgumentException if the name is empty
	 */
	public static void setLoginName(String name) throws NullPointerException, IllegalArgumentException {
		if (name == null) {
			throw new NullPointerException("Name cannot be null");
		}
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty!");
		}
		Client.name = name;
	}

	/**
	 * Logs out the client
	 */
	public static void logout() {
		instance = null;
	}

	/**
	 * Attempts to reconnect the player
	 */
	public static void attemptReconnect() {
		Timer reconnectTimer = new Timer("Reconnect timer");

		// the boolean to that checks whether the reconnection was successful
		AtomicBoolean reconnected = new AtomicBoolean(false);

		// the task that attempts to reconnect periodically
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// attempt to set up a new connection with the previous IP and port
				Platform.runLater(() -> NetworkManager.getInstance()
						.setup(ServerConnectionController.ip, ServerConnectionController.port,
								x -> {
									String content = "Connection error";
									if (x instanceof UnknownHostException || x instanceof NoRouteToHostException) {
										content = "Unknown host destination";
									} else if (x instanceof ConnectException) {
										content = "Could not connect to the server";
									}
									L.error(content);
								},
								() -> {
									// the connection was successful, send the server a hello packet with the previous
									// name
									L.info("Successfully connected to the server");
									try {
										NetworkManager.getInstance()
												.sendPacket(new PacketJustConnectedOutHello(
														ServerConnectionController.name));
									} catch (IllegalStateException | AnnotationTypeMismatchException | InvalidProtocolStateException ex) {
										L.error("Failed to send a packet!");
									}
									reconnected.set(true);
									cancel();
								}));
			}
		};

		// schedule the task
		reconnectTimer.schedule(task, 2000, 2000);

		// schedule another task that cancels the reconnection task, and disconnects the player if the server is
		// unreachable after 40 seconds
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				reconnectTimer.cancel();
				if (!reconnected.get()) {
					NetworkManager.getInstance()
							.disconnect("Disconnected", "Could not reconnect",
									"Failed to reconnect after a long timeout", true);
				}
			}
		}, 40000);
	}

	/**
	 * Logs in the client
	 *
	 * @throws IllegalStateException if the client is already logged in or the name is not yet set
	 */
	public static void login() throws IllegalStateException {
		if (instance != null) {
			throw new IllegalStateException("Already logged in as " + instance.getPlayer().getName());
		}
		if (name.isEmpty()) {
			throw new IllegalStateException("Haven't set the login name yet!");
		}
		L.info(String.format("Logging in as %s...", name));
		instance = new Client();
	}

	/**
	 * @return returns the player
	 */
	public ChessPlayer getPlayer() {
		return player;
	}

	/**
	 * Handles the en passant packet
	 *
	 * @param packet the en passant packet
	 */
	private void onEnPassant(PacketPlayInEnPassant packet) {
		final boolean white = getPlayer().isWhite();
		final Square pawnSquare = packet.getPawnSquare();

		// just DELETUS the pawn on the board
		getChessGame().getChessboard().setOnBoard(white ? pawnSquare :
				pawnSquare.flip(), ' ');
		update();
	}

	/**
	 * @return the chess game
	 */
	public ChessGame getChessGame() {
		return chessGame;
	}

	/**
	 * Updates the game UI
	 */
	private void update() {
		GameController.getInstance().draggableGrid.update();
	}

	/**
	 * Handles the castles packet
	 *
	 * @param packet the castles packet
	 */
	private void onCastles(PacketPlayInCastles packet) {
		final boolean clientWhite = getPlayer().isWhite();

		// check what castles it is
		boolean whiteCastles = packet.isWhite();
		boolean longCastles = packet.isLongCastles();

		// the rank is either 0 or 7 depending on the player's colour
		int rank = whiteCastles ? 0 : 7;

		// move the rook accordingly
		int rookFromFile = longCastles ? 0 : 7;
		int rookToFile = longCastles ? 3 : 5;

		// and the king as well
		int kingFromFile = 4;
		int kingToFile = longCastles ? 2 : 6;

		L.debug(String.format("white, long: %s, %s", whiteCastles, longCastles));
		moveCastles(clientWhite, rank, kingFromFile, kingToFile, "kingFrom - kingTo: %s - %s");
		moveCastles(clientWhite, rank, rookFromFile, rookToFile, "rookFrom - rookTo: %s - %s");
	}

	/**
	 * Castles the king
	 *
	 * @param clientWhite whether the client is white
	 * @param rank        the rank to castle
	 * @param fromFile    the file of the rook
	 * @param toFile      the target file of the rook
	 * @param s           the string format
	 */
	private void moveCastles(final boolean clientWhite, final int rank, final int fromFile, final int toFile,
	                         final String s) {
		final Square kingFrom = new Square(rank, fromFile);
		final Square kingTo = new Square(rank, toFile);

		L.info(String.format(s, kingFrom, kingTo));

		// the moves have to be flipped depending on the player's colour
		moveOnBoard(clientWhite ? kingFrom : kingFrom.flip(), clientWhite ? kingTo : kingTo.flip());
	}

	/**
	 * Moves a piece on the board and updates the board
	 *
	 * @param from square from
	 * @param to   square to
	 */
	private void moveOnBoard(Square from, Square to) {
		chessGame.getChessboard().moveOnBoard(from, to);
		update();
	}

	/**
	 * Handles the move response packet
	 *
	 * @param packet the move response packet
	 */
	private void onMoveResponse(PacketPlayInMoveResponse packet) {
		switch (packet.getResponseCode()) {
			case OK:
				break;
			case REJECTED:
				L.info("Attempted to play an invalid move! Move: " + PacketPlayOutMove.getMove(packet.getMoveId()));
				GameController.getInstance().appendMessage("Invalid move!");
				if (!onTurn) {
					GameController.getInstance().appendMessage("It's not your turn yet!");
				}
				break;
			default:
				throw new IllegalStateException("Invalid response received!");
		}
		update();
	}

	/**
	 * Handles the opponent disconnect packet
	 *
	 * @param packet opponent disconnect packet
	 */
	private void onOpponentDisconnect(PacketPlayInOpponentDisconnect packet) {
		// just alert the user that the opponent has disconnected
		Platform.runLater(() -> new AlertBuilder(Alert.AlertType.INFORMATION)
				.title("Opponent disconnected")
				.header("Please wait for the opponent to reconnect")
				.build()
				.show());
	}

	/**
	 * Handles the game finish packet
	 *
	 * @param packet game finish packet
	 */
	private void onGameFinish(PacketPlayInGameFinish packet) {
		// show the alert that someone won or the game ended in a draw
		Platform.runLater(() -> {
			AlertBuilder ab = new AlertBuilder(Alert.AlertType.INFORMATION)
					.title("Game finished!");

			final int fin = packet.getFinishType();
			if (fin == 0) {
				ab.header("Draw").content("Game ended in a draw.");
			} else {
				ab.header(String.format("%s won!",
								(fin & WIN_WHITE) != 0 == getPlayer().isWhite() ? "You" : "Opponent"))
						.content(String.format("Won by %s",
								(fin & WIN_BY_MATE) != 0 ? "mate" :
										(fin & WIN_BY_RESIGN) != 0 ? "resignation" :
												(fin & WIN_BY_TIME) != 0 ? "time" : "unknown circumstances"
						));
			}

			ab.build().showAndWait();

			// change the state to logged in
			NetworkManager.getInstance().changeState(ProtocolState.LOGGED_IN);
			SceneManager.changeScene(SceneManager.Scenes.PLAY_SCENE);
			chessGame = null;
		});
	}

	/**
	 * Starts the chess game
	 *
	 * @param chessGame the chess game to start
	 *
	 * @throws IllegalStateException if the user is already in a game
	 */
	public void startGame(ChessGame chessGame) throws IllegalStateException {
		if (this.chessGame != null) {
			throw new IllegalStateException("A chess game is already in play!");
		}
		this.chessGame = chessGame;
		L.info("Started a new game: " + chessGame);
		//startKeepAlive();
	}

	/**
	 * Handles the move packet
	 *
	 * @param packet the move packet
	 */
	private void onMove(final PacketPlayInMove packet) {
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();
		Square from = packet.getFrom();
		Square to = packet.getTo();
		GameController.getInstance().appendMessage(String.format("Move on board: %s-%s", from, to));

		// flip the coords if the player is black
		if (!clientWhite) {
			from = from.flip();
			to = to.flip();
		}

		// get the piece and set whose turn it is based on the moved piece
		final char piece = chessGame.getChessboard().getPieceId(from);
		setOnTurn((Character.toUpperCase(piece) == piece) != clientWhite);

		// move the piece on the board
		moveOnBoard(from, to);
		GameController.getInstance().appendMessage((onTurn ? "It's your turn" : "It's your opponent's turn"));
	}

	/**
	 * @return the client instance
	 *
	 * @throws IllegalStateException if the client is not yet logged in
	 */
	public static Client getClient() throws IllegalStateException {
		if (instance == null) {
			throw new IllegalStateException("Not yet logged in!");
		}
		return instance;
	}

	/**
	 * Changes the label text in the UI to whether the user is on turn
	 *
	 * @param onTurn whether the user is on turn
	 */
	public void setOnTurn(boolean onTurn) {
		this.onTurn = onTurn;
	}

	/**
	 * Sends a move to the server
	 *
	 * @param from the square from
	 * @param to   the square to
	 */
	public void sendServerMove(Square from, Square to) {
		final boolean clientWhite = Client.getClient().getPlayer().isWhite();

		// flip it for black
		from = clientWhite ? from : from.flip();
		to = clientWhite ? to : to.flip();

		// send the packet
		NetworkManager.getInstance().sendPacket(new PacketPlayOutMove(from, to));
	}

	/**
	 * Handles the draw offer packet
	 *
	 * @param packet the draw offer packet
	 */
	private void onDrawOffer(final PacketPlayInDrawOffer packet) {
		L.trace("Received a draw offer");
		final long millis = System.currentTimeMillis();
		if (millis - timeSinceLastDrawOffer > DRAW_OFFER_MAX_DELAY) {
			timeSinceLastDrawOffer = millis;
			L.debug("Showing draw offer available responses...");
			GameController.getInstance().appendMessage("Opponent asks for a draw");
		}
	}

	/**
	 * Handles the message packet
	 *
	 * @param packet the message packet
	 */
	private void onMessage(PacketPlayInMessage packet) {
		L.info("Message recvd: " + packet.getMessage());
		GameController.getInstance().appendMessage(packet.getMessage());
	}

	/**
	 * Handles the opponent name packet
	 *
	 * @param packet the opponent name packet
	 */
	private void onOpponentName(PacketPlayInOpponentName packet) {
		chessGame.setOpponent(new ChessPlayer(packet.getOpponentName()));
		Platform.runLater(() -> GameController.getInstance().opponent.setText(packet.getOpponentName()));
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

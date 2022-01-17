package jsmahy.ups_client.controller;

import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.game.ChessMove;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Square;

import java.util.Optional;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class DraggableGrid {

	private static final DataFormat FIGURE_FORMAT = new DataFormat("figure");
	private static final DataFormat SQUARE_FORMAT = new DataFormat("square");
	private static final DataFormat COLOUR_FORMAT = new DataFormat("colour");
	private final GridPane gridPane = new GridPane();

	private final Chessboard cb;

	public DraggableGrid(Chessboard cb) {
		this.cb = cb;
		final Image blackBG = new Image("/pieces/brown_cell.png", 50, 50, true, true);
		final Image whiteBG = new Image("/pieces/white_cell.png", 50, 50, true, true);


		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				final Group group = new Group();
				final boolean black = ((x + y) % 2) == 0;
				final Image bg = black ? blackBG : whiteBG;
				final ImageView bgImageView = new ImageView(bg);
				final Square sq = new Square(y, x);
				final Optional<IChessPiece> opt = cb.getPiece(sq);

				final boolean pieceColour = opt.isPresent() && ChessPieceUtil.isWhite(cb.getPieceId(sq));
				final Image img = opt.map(m -> m.getImage(pieceColour)).orElse(null);
				final ImageView imageView = new ImageView(img);

				group.setOnDragDetected(e -> {
					final Optional<IChessPiece> currPieceOpt = cb.getPiece(sq);

					if (currPieceOpt.isEmpty()) {
						e.consume();
						return;
					}
					final IChessPiece currPiece = currPieceOpt.get();

					final boolean currPieceColour = ChessPieceUtil.isWhite(cb.getPieceId(sq));
					final Image currImg = currPiece.getImage(currPieceColour);

					final Dragboard dragboard = imageView.startDragAndDrop(TransferMode.MOVE);
					dragboard.setDragView(currImg, currImg.getWidth() / 2d, currImg.getHeight() / 2d);
					final ClipboardContent clipboardContent = new ClipboardContent();
					clipboardContent.put(FIGURE_FORMAT, currPieceOpt.get());
					clipboardContent.put(SQUARE_FORMAT, sq);
					clipboardContent.put(COLOUR_FORMAT, currPieceColour);
					imageView.setImage(null);
					dragboard.setContent(clipboardContent);
					e.consume();
				});

				group.setOnDragOver(e -> {
					if (e.getGestureSource() != group) {
						e.acceptTransferModes(TransferMode.MOVE);
					}
				});

				group.setOnDragDropped(e -> {
					final Dragboard dragboard = e.getDragboard();
					final ImageView node = (ImageView) group.getChildren().get(1);
					final ImageView gestureSource = (ImageView) e.getGestureSource();
					final IChessPiece piece = (IChessPiece) dragboard.getContent(FIGURE_FORMAT);
					final Square from = (Square) dragboard.getContent(SQUARE_FORMAT);

					if (piece == null) {
						e.consume();
						return;
					}

					final boolean currPieceColour = ChessPieceUtil.isWhite(cb.getPieceId(from));
					final Image image = piece.getImage(currPieceColour);
					if (gestureSource.equals(node)) {
						node.setImage(image);
						e.consume();
						return;
					}

					System.out.println("Drag Drop:");
					System.out.println(from + " -> " + sq);
					Client.getClient().move(from, sq);
					e.setDropCompleted(true);
					e.consume();
				});

				group.setOnDragDone(Event::consume);

				group.getChildren().addAll(bgImageView, imageView);
				gridPane.add(group, x, 7 - y);
			}
		}

		gridPane.setGridLinesVisible(false);

		// set width and height to 8 * 50 (image w/h) + 2 (for border stroke)
		gridPane.setMaxWidth(402);
		gridPane.setMinWidth(402);
		gridPane.setMaxHeight(402);
		gridPane.setMinHeight(402);
		gridPane.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));

	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void update() {
		final boolean clientBlack = !Client.getClient().getPlayer().isWhite();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {

				int index = (y * 8 + x);
				final Group group = (Group) gridPane.getChildren().get(index);
				final ImageView imageView = (ImageView) group.getChildren().get(1);
				final Square pos = new Square(y, x);
				final Optional<IChessPiece> figure = cb.getPiece(pos);
				if (figure.isEmpty()) {
					imageView.setImage(null);
					continue;
				}
				imageView.setImage(figure.get().getImage(ChessPieceUtil.isWhite(cb.getPieceId(pos))));
			}
		}
	}

	public void moveOnGrid(Square from, Square to) {
		cb.getPiece(from).ifPresent(x -> {
			updateImage(null, from);
			updateImage(x.getImage(ChessPieceUtil.isWhite(cb.getPieceId(from))), to);
		});
	}

	private void updateImage(Image image, Square position) {
		// 0 = left white rook
		// 7 = right white rook
		// 8-15 = white pawns
		// 48-55 = black pawns
		// 56 = left black rook
		// 63 = right black rook
		int index = (position.getRank() * 8 + position.getFile());

		Group group = (Group) gridPane.getChildren().get(index);

		final ImageView toImage = (ImageView) group.getChildren().get(1);
		toImage.setImage(image);
	}
}

package jsmahy.ups_client.controller;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.util.Square;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class DraggableGrid {
	private static final Logger L = LogManager.getLogger(DraggableGrid.class);

	private static final DataFormat PIECE = new DataFormat("figure");
	private static final DataFormat SQUARE_FORMAT = new DataFormat("square");
	private static final DataFormat COLOUR_FORMAT = new DataFormat("colour");
	private static final HashMap<Character, Image> PIECE_IMAGES = new HashMap<>() {
		{
			// yes this is awful but idgaf anymore
			put('p', createImage("pawn", false));
			put('P', createImage("pawn", true));

			put('r', createImage("rook", false));
			put('R', createImage("rook", true));

			put('n', createImage("knight", false));
			put('N', createImage("knight", true));

			put('b', createImage("bishop", false));
			put('B', createImage("bishop", true));

			put('k', createImage("king", false));
			put('K', createImage("king", true));

			put('q', createImage("queen", false));
			put('Q', createImage("queen", true));
		}
	};
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
				final char pieceId = cb.getPieceId(sq);

				final Image img = PIECE_IMAGES.get(pieceId);
				final ImageView imageView = new ImageView(img);

				group.setOnDragDetected(e -> {
					L.debug("Started dragging");
					final char piece = cb.getPieceId(sq);
					final Image currImg = getImage(piece);

					// no piece
					if (currImg == null) {
						L.debug("No image, stopped dragging");
						e.consume();
						return;
					}

					L.debug("Image found, dragging...");
					final Dragboard dragboard = imageView.startDragAndDrop(TransferMode.MOVE);
					dragboard.setDragView(currImg, currImg.getWidth() / 2d, currImg.getHeight() / 2d);

					final ClipboardContent clipboardContent = new ClipboardContent();
					clipboardContent.put(PIECE, piece);
					clipboardContent.put(SQUARE_FORMAT, sq);

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
					L.debug("Drag dropped");
					final Dragboard dragboard = e.getDragboard();
					final ImageView imgView = (ImageView) group.getChildren().get(1);
					final ImageView gestureSource = (ImageView) e.getGestureSource();

					final Image image = getImage((char) dragboard.getContent(PIECE));
					final Square from = (Square) dragboard.getContent(SQUARE_FORMAT);

					if (gestureSource.equals(imgView)) {
						L.debug("Same gesture source to image view");
						imgView.setImage(image);
						e.consume();
						return;
					}

					L.debug("Drag drop - move");
					System.out.println("Drag Drop:");
					System.out.println(from + " -> " + sq);
					Client.getClient().sendServerMove(from, sq);
					e.setDropCompleted(true);
					e.consume();
					update();
				});

				group.setOnDragDone(e -> update());

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

	private static Image getImage(char piece) {
		return PIECE_IMAGES.get(piece);
	}

	private static Image createImage(String piece, boolean white) {
		return new Image(String.format("/pieces/%s_%s.png", piece, white ? "white" : "black"), 50, 50, true, true);
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void update() {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				int index = (y * 8 + x);
				final Group group = (Group) gridPane.getChildren().get(index);
				final ImageView imageView = (ImageView) group.getChildren().get(1);
				final Square pos = new Square(y, x);
				final Image img = getImage(cb.getPieceId(pos));
				imageView.setImage(img);
			}
		}
	}
}

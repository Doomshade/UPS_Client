package jsmahy.ups_client.game;

import jsmahy.ups_client.exception.InvalidFENFormatException;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Square;
import jsmahy.ups_client.util.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.regex.Matcher;

import static java.lang.String.format;

public final class Chessboard {

	private static final Logger L = LogManager.getLogger(Chessboard.class);
	// board of characters
	// upper case 'P' represents a white pawn, lower case 'p' represents a black pawn
	// this goes for all the pieces
	private final char[][] board = new char[ChessPieceUtil.ROW_SIZE][ChessPieceUtil.ROW_SIZE];

	/**
	 * Sets up a board from a fen string.
	 *
	 * @param fen the fen string
	 *
	 * @throws InvalidFENFormatException if the fen string is invalid
	 */
	public void setupBoard(String fen) throws InvalidFENFormatException {
		// somewhat of an ugly pattern but works
		// this just ensures the format is right
		final Matcher m = Util.FEN_PATTERN.matcher(fen);

		// very likely an invalid fen string
		if (!m.find()) {
			throw new InvalidFENFormatException(format("Invalid FEN String %s", fen));
		}

		// start parsing the string
		// the first group is the big part
		// rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
		final String[] split = m.group(1).split("/");
		L.trace(format("First part of FEN: %s", Arrays.toString(split)));
		if (split.length != 8) {
			throw new InvalidFENFormatException("Attempted to parse an invalid FEN String");
		}

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				board[i][j] = ' ';
			}

		}
		for (int i = 0; i < split.length; i++) {
			int rowIdx = 0;
			for (char c : split[i].toCharArray()) {
				// check how many row idx we skip
				if (Character.isDigit(c)) {
					rowIdx += Character.digit(c, 10);
				} else {
					// there could be too large of a number
					// or the piece id is nonexistent
					if (rowIdx >= 8 || !ChessPieceUtil.isPiece(c)) {
						throw new InvalidFENFormatException(
								"Attempted to parse an invalid FEN String");
					}
					// there should be only valid characters now
					// 7 - i because FEN string indexes it from the top, while we index it from the bottom
					final boolean white = Client.getClient().getPlayer().isWhite();
					final int col = white ? 7 - i : i;
					final int row = white ? rowIdx : 7 - rowIdx;
					board[col][row] = c;
					rowIdx++;
				}
			}
		}
		final int rowSize = ChessPieceUtil.ROW_SIZE;
		for (int i = rowSize - 1; i >= 0; i--) {
			char[] bbuf = new char[8];
			for (int j = 0; j < rowSize; j++) {
				char c = board[i][j];
				if (c == '\u0000') {
					c = ' ';
				}
				bbuf[j] = c;
			}
		}
		String wb = m.group(4);
		L.trace("Chessboard after first FEN part: " + Arrays.deepToString(board));
		// end of chessboard piece parsing

		L.info("Successfully set up chessboard. Board:\n" + Arrays.deepToString(board));
	}

	/**
	 * Moves the piece on the board after all the validation is done
	 *
	 * @param from the starting pos
	 * @param to   the ending pos
	 */
	public void moveOnBoard(Square from, Square to) {
		L.trace(format("Moving from %s to %s", from, to));
		setOnBoard(to, board[from.getRank()][from.getFile()]);
		setOnBoard(from, ' ');

		L.debug("Current board state:");
		L.debug(Arrays.deepToString(board));
	}

	public void setOnBoard(Square sq, char piece) {
		board[sq.getRank()][sq.getFile()] = piece;
	}

	/**
	 * Returns the piece ID on the given square.
	 *
	 * @param pos the square to look for
	 *
	 * @return the piece ID
	 */
	public char getPieceId(Square pos) {
		return board[pos.getRank()][pos.getFile()];
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("board", board)
				.toString();
	}
}

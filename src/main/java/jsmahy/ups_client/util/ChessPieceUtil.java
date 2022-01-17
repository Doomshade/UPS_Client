package jsmahy.ups_client.util;

/**
 * Chess piece utility class.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public final class ChessPieceUtil {

	/**
	 * The row/column size.
	 */
	public static final int ROW_SIZE = 8;

	public static final String ALL_PIECE_IDS = "pbnrqkPBNRQK";

	public static boolean isPiece(char c) {
		return ALL_PIECE_IDS.indexOf(c) != -1;
	}

}

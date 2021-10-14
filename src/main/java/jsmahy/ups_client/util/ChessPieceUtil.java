package jsmahy.ups_client.util;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;

import java.util.regex.Pattern;

/**
 * Chess piece utility class.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public final class ChessPieceUtil {

    /**
     * The starting position.
     */
    public static final String START_FEN =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /**
     * The FEN pattern.
     */
    public static final Pattern FEN_PATTERN =
            Pattern.compile(
                    "((([rnbqkpRNBQKP1-8]+)\\/){7}([rnbqkpRNBQKP1-8]+)) ([wb]) (K?Q?k?q?|\\-) (" +
                            "([a-h][0-7])|\\-) (\\d+) (\\d+)");

    /**
     * The row/column size.
     */
    public static final int ROW_SIZE = 8;

    /**
     * Validates the char piece ID.
     *
     * @param id the ID
     *
     * @throws IllegalArgumentException if the ID is invalid
     */
    private static void validateId(char id) throws IllegalArgumentException {
        if (!isPiece(id)) {
            throw new IllegalArgumentException(String.format("Invalid ID '%c' provided", id));
        }
    }

    /**
     * Checks whether both pieces are the same colour
     *
     * @param a the first piece
     * @param b the second piece
     *
     * @return {@code true} if both pieces are the same colour
     *
     * @throws IllegalArgumentException if a piece identifier is invalid
     */
    public static boolean areSameColours(char a, char b) throws IllegalArgumentException {
        return isWhite(a) == isWhite(b);
    }

    /**
     * Checks whether the piece is white.
     *
     * @param c the piece identifier
     *
     * @return {@code true} if the piece is white
     *
     * @throws IllegalArgumentException if the piece identifier is invalid
     */
    public static boolean isWhite(char c) throws IllegalArgumentException {
        validateId(c);
        return Character.isUpperCase(c);
    }

    /**
     * Converts a char to represent a black piece.
     *
     * @param c the piece identifier
     *
     * @return the black piece identifier
     */
    public static char toBlack(char c) {
        validateId(c);
        return Character.toLowerCase(c);
    }

    /**
     * Converts a char to represent a white piece.
     *
     * @param c the piece identifier
     *
     * @return the white piece identifier
     */
    public static char toWhite(char c) {
        validateId(c);
        return Character.toUpperCase(c);
    }

    /**
     * @return all valid piece identifiers
     */
    public static String getAllPieceIds() {
        return "pbnrqkPBNRQK";
    }

    public static boolean isPiece(char c) {
        return getAllPieceIds().indexOf(c) != -1;
    }

    /**
     * @param id the piece identifier
     *
     * @return a chess piece
     *
     * @throws IllegalArgumentException if the piece identifier is invalid
     * @throws IllegalStateException    if the piece is valid but not found
     */
    public static IChessPiece getPieceById(char id)
            throws IllegalArgumentException, IllegalStateException {
        validateId(id);
        id = toBlack(id);
        for (ChessPieceEnum e : ChessPieceEnum.values()) {
            if (e.getPiece().getBlack() == id) {
                return e.getPiece();
            }
        }
        throw new IllegalStateException(String.format("Could not find a piece with ID '%c'", id));
    }

    /**
     * @param piece the piece to check for
     *
     * @return {@code true} if the given piece is a king
     */
    public static boolean isKing(IChessPiece piece) {
        return is(piece, ChessPieceEnum.KING);
    }

    /**
     * Checks whether a piece corresponds to a piece in the enum
     *
     * @param piece     the piece
     * @param whatPiece the piece to compare to
     *
     * @return {@code true} if the pieces correspond to each other
     */
    public static boolean is(IChessPiece piece, ChessPieceEnum whatPiece) {
        if (piece == null || whatPiece == null) {
            return false;
        }
        return piece.equals(whatPiece.getPiece());
    }

    /**
     * Validates the position.
     *
     * @param row    the row
     * @param column the column
     *
     * @throws IllegalArgumentException if the position is invalid
     */
    public static void validatePosition(int row, int column) throws IllegalArgumentException {
        if (!isValidPosition(row, column)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Both parameters must be within the range of 0-7 (inclusive)! row=%d," +
                                    " column=%d", row, column));
        }
    }

    /**
     * Checks whether the row and column are valid.
     *
     * @param row    the row
     * @param column the column
     *
     * @return {@code true} if both arguments are within 0-7 (inclusive) range
     */
    public static boolean isValidPosition(int row, int column) {
        // only the 3 lowest significant bits are needed
        return (row >> 3) == 0 && (column >> 3) == 0;
    }
}
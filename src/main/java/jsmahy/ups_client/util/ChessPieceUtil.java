package jsmahy.ups_client.util;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.game.Chessboard;

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
        return Character.isLowerCase(c);
    }

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

    public static boolean isPiece(char c) {
        return getAllPieceIds().indexOf(c) != -1;
    }

    /**
     * @return all valid piece identifiers
     */
    public static String getAllPieceIds() {
        return "pbnrqkPBNRQK";
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
        return Character.toUpperCase(c);
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
     * Checks whether the target piece is on the given square.
     *
     * @param chessboard  the chessboard
     * @param targetPiece the target piece
     * @param square      the square
     *
     * @return {@code true} if there is a piece, the pieces match, and the colours are the same
     */
    public static boolean isCorrectPieceOnSquare(Chessboard chessboard, ChessPieceEnum targetPiece,
                                                 Square square) {
        if (!chessboard.containsPiece(square)) {
            return false;
        }

        IChessPiece piece = targetPiece.getPiece();
        char pieceId = chessboard.getPieceId(square);

        if (isWhite(pieceId)) {
            return piece.getWhite() == pieceId;
        } else {
            return piece.getBlack() == pieceId;
        }
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
        return Character.toLowerCase(c);
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
     * Checks whether the rank and file are valid.
     *
     * @param rank the rank
     * @param file the file
     *
     * @return {@code true} if both arguments are within 0-7 (inclusive) range
     * @see Square#isValidPosition(int, int)
     */
    public static boolean isValidPosition(int rank, int file) {
        return Square.isValidPosition(rank, file);
    }
}

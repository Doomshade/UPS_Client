package jsmahy.ups_client.util;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;

public final class ChessPieceUtil {

    /**
     * Validates the char piece ID
     *
     * @param id
     * @throws IllegalArgumentException if the ID is invalid
     */
    private static void validateId(char id) throws IllegalArgumentException {
        if (getAllPieceIds().indexOf(id) == -1) {
            throw new IllegalArgumentException(String.format("Invalid ID '%c' provided", id));
        }
    }

    public static boolean isWhite(char c) throws IllegalArgumentException {
        validateId(c);
        return Character.isUpperCase(c);
    }

    /**
     * Converts a char to represent a black piece
     *
     * @param c the piece char
     * @return
     */
    public static char toBlack(char c) {
        validateId(c);
        return Character.toLowerCase(c);
    }

    /**
     * Converts a char to represent a white piece
     *
     * @param c the piece char
     * @return
     */
    public static char toWhite(char c) {
        validateId(c);
        return Character.toUpperCase(c);
    }

    public static String getAllPieceIds() {
        return "pbnrqkPBNRQK";
    }

    public static IChessPiece getPieceById(char id) throws IllegalArgumentException, IllegalStateException {
        validateId(id);
        id = toBlack(id);
        for (ChessPieceEnum e : ChessPieceEnum.values()) {
            if (e.getPiece().getBlack() == id) {
                return e.getPiece();
            }
        }
        throw new IllegalStateException(String.format("Could not find a piece with ID '%c'", id));
    }

    public static boolean isKing(IChessPiece piece) {
        return is(piece, ChessPieceEnum.KING);
    }

    public static boolean is(IChessPiece piece, ChessPieceEnum whatPiece) {
        return piece.equals(whatPiece.getPiece());
    }

}

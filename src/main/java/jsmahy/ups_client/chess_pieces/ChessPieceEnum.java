package jsmahy.ups_client.chess_pieces;

/**
 * The implementation of chess pieces.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public enum ChessPieceEnum {
    /**
     * The Pawn
     */
    PAWN(new Pawn()),
    /**
     * The Bishop
     */
    BISHOP(new Bishop()),
    /**
     * The kNight
     */
    KNIGHT(new Knight()),
    /**
     * The Rook
     */
    ROOK(new Rook()),
    /**
     * The Queen
     */
    QUEEN(new Queen()),
    /**
     * The King
     */
    KING(new King());

    private final IChessPiece piece;

    ChessPieceEnum(IChessPiece piece) {
        this.piece = piece;

    }

    /**
     * @return the piece
     */
    public IChessPiece getPiece() {
        return piece;
    }


}

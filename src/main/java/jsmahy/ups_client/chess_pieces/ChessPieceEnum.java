package jsmahy.ups_client.chess_pieces;

/**
 * The enum Chess piece enum.
 */
public enum ChessPieceEnum {
    /**
     * The Pawn
     */
    PAWN(new Pawn()),
    /**
     * The Bishop
     */
    BISHOP(null),
    /**
     * The kNight
     */
    KNIGHT(null),
    /**
     * The Rook
     */
    ROOK(null),
    /**
     * The Queen
     */
    QUEEN(null),
    /**
     * The King
     */
    KING(null);

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

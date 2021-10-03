package jsmahy.ups_client.chess_pieces;

/**
 * The enum Chess piece enum.
 */
public enum ChessPieceEnum {
    /**
     * The pawn
     */
    PAWN(new Pawn()),
    /**
     * The bishop
     */
    BISHOP(null),
    /**
     * The knight
     */
    KNIGHT(null),
    /**
     * The rook
     */
    ROOK(null),
    /**
     * The queen
     */
    QUEEN(null),
    /**
     * The king
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

package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Square;

import java.util.Collection;

class Rook extends AbstractChessPiece {
    Rook() {
        super('r');
    }

    @Override
    public Collection<Square> getValidMoves(final Chessboard chessboard,
                                            final Square currentSquare) {
        return generateMoves(chessboard, currentSquare, Direction.HOR_AND_VERT,
                ChessPieceUtil.ROW_SIZE);
    }
}

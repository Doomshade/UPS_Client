package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Square;

import java.util.Collection;

class Queen extends AbstractChessPiece {
    Queen() {
        super('q');
    }

    @Override
    public Collection<Square> getValidMoves(final Chessboard chessboard,
                                            final Square currentSquare) {
        Collection<Square> moves = generateMoves(chessboard, currentSquare,
                Direction.DIAGONAL, ChessPieceUtil.ROW_SIZE);
        moves.addAll(generateMoves(chessboard, currentSquare, Direction.HOR_AND_VERT,
                ChessPieceUtil.ROW_SIZE));
        return moves;
    }
}

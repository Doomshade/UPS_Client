package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Position;

import java.util.Collection;

class Queen extends AbstractChessPiece {
    Queen() {
        super('q');
    }

    @Override
    public Collection<Position> getValidMoves(final Chessboard chessboard,
                                              final Position currentPosition) {
        Collection<Position> moves = generateMoves(chessboard, currentPosition,
                Direction.DIAGONAL, ChessPieceUtil.ROW_SIZE);
        moves.addAll(generateMoves(chessboard, currentPosition, Direction.HOR_AND_VERT,
                ChessPieceUtil.ROW_SIZE));
        return moves;
    }
}

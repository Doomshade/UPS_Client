package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Position;

import java.util.Collection;

class Rook extends AbstractChessPiece {
    Rook() {
        super('r');
    }

    @Override
    public Collection<Position> getValidMoves(final Chessboard chessboard,
                                              final Position currentPosition) {
        return generateMoves(chessboard, currentPosition, Direction.HOR_AND_VERT,
                ChessPieceUtil.ROW_SIZE);
    }
}

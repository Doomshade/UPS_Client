package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Position;

import java.util.Collection;

class Bishop extends AbstractChessPiece {
    Bishop() {
        super('b');
    }

    @Override
    public Collection<Position> getValidMoves(final Chessboard chessboard,
                                              final Position currentPosition) {
        return generateMoves(chessboard, currentPosition, Direction.DIAGONAL);
    }
}

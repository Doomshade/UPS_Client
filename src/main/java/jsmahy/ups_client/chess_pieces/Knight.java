package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Square;

import java.util.Collection;

class Knight extends AbstractChessPiece {
    Knight() {
        super('n');
    }

    @Override
    public Collection<Square> getValidMoves(final Chessboard chessboard,
                                            final Square currentSquare) {
        return null;
    }
}

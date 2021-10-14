package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Position;

import java.util.Collection;

class Knight extends AbstractChessPiece {
    Knight() {
        super('n');
    }

    @Override
    public Collection<Position> getValidMoves(final Chessboard chessboard,
                                              final Position currentPosition) {
        return null;
    }
}

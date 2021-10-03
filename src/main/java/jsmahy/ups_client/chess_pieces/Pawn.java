package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Position;

import java.util.Collection;

/**
 * The type Pawn.
 */
class Pawn extends AbstractChessPiece {
    /**
     * Instantiates a new Pawn.
     */
    public Pawn() {
        super((byte) 1);
    }

    @Override
    public Collection<Position> getValidMoves(Chessboard chessboard, Position currentPosition) {
        if (!chessboard.hasMoved(currentPosition)) {

        }
        return null;
    }
}

package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Position;

import java.util.Collection;
import java.util.HashSet;

/**
 * The type Pawn.
 */
class Pawn extends AbstractChessPiece {

    Pawn() {
        super('p');
    }

    @Override
    public Collection<Position> getValidMoves(Chessboard chessboard, Position currentPosition) {
        Collection<Position> validMoves = new HashSet<>();

        addMoves(chessboard, currentPosition, validMoves);
        addAttackingMoves(chessboard, currentPosition, validMoves);

        return validMoves;
    }

    private void addAttackingMoves(Chessboard chessboard, Position currentPosition,
                                   Collection<Position> validMoves) {

    }

    private void addMoves(Chessboard chessboard, Position currentPosition,
                          Collection<Position> validMoves) {
        // check if there's a piece one square up
        boolean canMoveTwoSquares = canMoveTwoSquares(chessboard, currentPosition);
    }

    private boolean canMoveTwoSquares(Chessboard chessboard, Position currentPosition) {
        boolean canMoveTwice = false;
        if (chessboard.isWhite(currentPosition)) {
            if (currentPosition.getRow() == 1) {
                canMoveTwice = true;
            }
        } else if (currentPosition.getRow() == 6) {
            canMoveTwice = true;
        }
        return canMoveTwice;
    }
}

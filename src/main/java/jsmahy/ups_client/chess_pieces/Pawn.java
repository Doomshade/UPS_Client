package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Pair;
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

        byte row = chessboard.isWhite(currentPosition) ? (byte) 1 : (byte) -1;
        final Pair[] vectors = {new Pair(row, 0)};
        final Collection<Position> c = generateMoves(chessboard, currentPosition, vectors, 1);
        validMoves.addAll(c);
        return validMoves;
    }

    private void addAttackingMoves(Chessboard chessboard, Position currentPosition,
                                   Collection<Position> validMoves) {
        // TODO add en passant
        byte row = chessboard.isWhite(currentPosition) ? (byte) 1 : (byte) -1;
        try {
            Position right = currentPosition.add(row, (byte) 1);
            Position left = currentPosition.add(row, (byte) -1);

            addAttackingMove(chessboard, currentPosition, validMoves, left);
            addAttackingMove(chessboard, currentPosition, validMoves, right);
        } catch (IllegalArgumentException e) {
            // ignore the ex
        }
    }

    private void addAttackingMove(final Chessboard chessboard, final Position currentPosition,
                                  final Collection<Position> validMoves, final Position pos) {
        if (!ChessPieceUtil.areSameColours(chessboard.getPieceId(currentPosition),
                chessboard.getPieceId(pos))) {
            validMoves.add(pos);
        }
    }

    private void addMoves(Chessboard chessboard, Position currentPosition,
                          Collection<Position> validMoves) {
        // check if there's a piece one square up
        boolean canMoveTwoSquares = canMoveTwoSquares(chessboard, currentPosition);
        byte by = chessboard.isWhite(currentPosition) ? (byte) 2 : (byte) -2;
        validMoves.add(currentPosition.add(by, (byte) 0));
    }

    private boolean canMoveTwoSquares(Chessboard chessboard, Position currentPosition) {
        // is white and 2nd row
        if (chessboard.isWhite(currentPosition) && currentPosition.getRow() == 1) {
            return true;
        } else {
            // is black and 7th row
            return !chessboard.isWhite(currentPosition) && currentPosition.getRow() == 6;
        }
    }
}

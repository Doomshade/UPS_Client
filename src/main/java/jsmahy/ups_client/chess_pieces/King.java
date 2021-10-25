package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Square;

import java.util.Collection;
import java.util.HashSet;

class King extends AbstractChessPiece {
    private static final Square DEFAULT_WHITE_KING_SQUARE = new Square(0, 4);
    private static final Square DEFAULT_BLACK_KING_SQUARE = new Square(7, 4);

    King() {
        super('k');
    }

    @Override
    public Collection<Square> getValidMoves(final Chessboard chessboard,
                                            final Square currentSquare) {
        Collection<Square> moves = new HashSet<>();

        addCastles(chessboard, currentSquare, moves);
        addNormalMoves(chessboard, currentSquare, moves);
        return moves;
    }

    /**
     * Adds the normal valid moves to the collection.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the current square of the king
     * @param moves         the collection
     */
    private void addNormalMoves(final Chessboard chessboard, final Square currentSquare,
                                final Collection<Square> moves) {
        Pair[] vectors = {
                new Pair<>(0, 1),
                new Pair<>(0, -1),
                new Pair<>(1, 0),
                new Pair<>(-1, 0)
        };
        moves.addAll(generateMoves(chessboard, currentSquare,
                // cast it to the generic argument so the method actually returns a collection
                // with a generic argument
                // java in a nutshell
                (Pair<Integer, Integer>[]) vectors, 1));
    }

    /**
     * Adds the castles moves to the collection if the king is able to castle.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the current position of the piece
     * @param moves         the collection
     */
    private void addCastles(final Chessboard chessboard, final Square currentSquare,
                            final Collection<Square> moves) {
        boolean white = chessboard.isWhite(currentSquare);
        Square defaultPos = white ? DEFAULT_WHITE_KING_SQUARE : DEFAULT_BLACK_KING_SQUARE;

        // short castles
        if (chessboard.getAllowedCastles(white, true)) {
            moves.add(defaultPos.add((byte) 0, (byte) 2));
        }

        // long castles
        if (chessboard.getAllowedCastles(white, false)) {
            moves.add(defaultPos.add((byte) 0, (byte) -2));
        }

    }
}

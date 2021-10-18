package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Position;

import java.util.Collection;
import java.util.HashSet;

class King extends AbstractChessPiece {
    private static final Position DEFAULT_WHITE_KING_POSITION = new Position((byte) 0, (byte) 4);
    private static final Position DEFAULT_BLACK_KING_POSITION = new Position((byte) 7, (byte) 4);

    King() {
        super('k');
    }

    @Override
    public Collection<Position> getValidMoves(final Chessboard chessboard,
                                              final Position currentPosition) {
        Collection<Position> moves = new HashSet<>();
        final boolean white = chessboard.isWhite(currentPosition);
        Position defaultPos = white ? DEFAULT_WHITE_KING_POSITION : DEFAULT_BLACK_KING_POSITION;
        if (currentPosition.equals(defaultPos)) {
            if (chessboard.getAllowedCastles(white, true)) {
                moves.add(defaultPos.add((byte) 0, (byte) 2));
            }

            if (chessboard.getAllowedCastles(white, false)) {
                moves.add(defaultPos.add((byte) 0, (byte) -2));
            }
        }
        final Pair[] vectors = {
                new Pair<>(0, 1),
                new Pair<>(0, -1),
                new Pair<>(1, 0),
                new Pair<>(-1, 0)
        };
        moves.addAll(generateMoves(chessboard, currentPosition, vectors, 1));
        return moves;
    }
}

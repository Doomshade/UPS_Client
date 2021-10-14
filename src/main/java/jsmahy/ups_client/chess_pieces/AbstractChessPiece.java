package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Position;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * The abstract implementation of a chess piece,
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
abstract class AbstractChessPiece implements IChessPiece {
    private final char white;
    private final char black;

    AbstractChessPiece(char charId) {
        this.white = ChessPieceUtil.toWhite(charId);
        this.black = ChessPieceUtil.toBlack(charId);
    }

    /**
     * The possible directions in vector.
     */
    protected enum Direction {
        /**
         * The diagonal direction.
         */
        DIAGONAL(new Pair<>(1, 1)),
        /**
         * The horizontal and vertical direction.
         */
        HOR_AND_VERT(new Pair<>(0, 1), new Pair<>(1, 0));

        private final Pair<Integer, Integer>[] vectors;

        @SafeVarargs
        Direction(Pair<Integer, Integer>... vectors) {
            this.vectors = vectors;
        }
    }

    /**
     * Generates valid moves.
     *
     * @param chessboard the chessboard
     * @param piecePos   the piece position
     * @param direction  the direction
     *
     * @return all valid moves, be it captures or castles
     */
    protected Collection<Position> generateMoves(Chessboard chessboard, Position piecePos,
                                                 Direction direction) {
        Collection<Position> positions = new HashSet<>();
        if (!chessboard.containsPiece(piecePos)) {
            return positions;
        }
        final char givenPieceId = chessboard.getPieceId(piecePos);
        boolean givenPieceColour = chessboard.isWhite(piecePos);

        Position copy = new Position(piecePos);
        for (Pair<Integer, Integer> vector : direction.vectors) {
            // check for all direction combinations
            for (int i = 0; i < 4; i++) {
                // multiplies the vector by 0 1 0 1
                int a = vector.a * (i & 0b1);

                // multiplies the vector by 0 0 1 1
                int b = vector.b * (i & 0b10);

                // don't check for invalid positions
                while (ChessPieceUtil.isValidPosition(a, b)) {
                    // generate a new position
                    Position pos = new Position((byte) a, (byte) b);
                    char newPieceId = chessboard.getPieceId(pos);
                    // move in the direction vector
                    a++;
                    b++;
                }

            }
        }
        return positions;
    }

    @Override
    public final boolean isValidMove(Chessboard chessboard, Position currentPosition,
                                     Position destination) {
        return getValidMoves(chessboard, currentPosition).contains(destination);
    }

    @Override
    public final char getWhite() {
        return white;
    }

    @Override
    public final char getBlack() {
        return black;
    }
}
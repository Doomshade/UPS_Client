package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Position;

import java.util.Collection;
import java.util.HashSet;

/**
 * The abstract implementation of a chess piece.
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

    protected Collection<Position> generateMoves(Chessboard chessboard, Position piecePos,
                                                 Direction direction, int amount) {
        return generateMoves(chessboard, piecePos, direction.vectors, amount);
    }

    /**
     * Generates valid moves.
     *
     * @param chessboard the chessboard
     * @param piecePos   the piece position
     * @param vectors    the vectors
     *
     * @return all valid moves, be it captures or castles
     */
    protected Collection<Position> generateMoves(Chessboard chessboard, Position piecePos,
                                                 Pair<Integer, Integer>[] vectors, int amount) {
        Collection<Position> positions = new HashSet<>();
        if (!chessboard.containsPiece(piecePos)) {
            return positions;
        }
        final char givenPieceId = chessboard.getPieceId(piecePos);
        boolean givenPieceColour = chessboard.isWhite(piecePos);

        for (Pair<Integer, Integer> vector : vectors) {
            // check for all vectors combinations
            for (int i = 0; i < 4; i++) {
                // multiplies the vector by 0 1 0 1
                int a = vector.a * (i & 0b1);

                // multiplies the vector by 0 0 1 1
                int b = vector.b * (i & 0b10);

                int count = 0;
                // don't check for invalid positions
                while (ChessPieceUtil.isValidPosition(a, b) && count++ != amount) {
                    // generate a new position
                    Position pos;
                    try {
                        pos = new Position((byte) a, (byte) b);
                    } catch (IllegalArgumentException e) {
                        // the position is no longer valid, stop searching
                        break;
                    }
                    try {
                        char targetSquarePieceId = chessboard.getPieceId(pos);

                        // a piece found in the vectors, if the target square is
                        // the opponents piece, add it to the valid moves
                        if (!ChessPieceUtil.areSameColours(targetSquarePieceId, givenPieceId)) {
                            positions.add(pos);
                        }
                        break;
                    } catch (IllegalArgumentException ignored) {
                        // no piece was found, add the position and move in the vectors vector
                        positions.add(pos);
                        a++;
                        b++;
                    }
                }

            }
        }
        return positions;
    }

    @Override
    public final boolean isValidMove(Chessboard chessboard, Position currentPosition,
                                     Position destination) {
        final Collection<Position> validMoves = getValidMoves(chessboard, currentPosition);
        if (validMoves == null) {
            throw new UnsupportedOperationException("Moves for this piece were not yet " +
                    "implemented!");
        }
        return validMoves.contains(destination);
    }

    @Override
    public final char getWhite() {
        return white;
    }

    @Override
    public final char getBlack() {
        return black;
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
}

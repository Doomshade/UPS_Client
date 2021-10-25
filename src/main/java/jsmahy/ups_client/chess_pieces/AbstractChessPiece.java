package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Square;

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

    protected Collection<Square> generateMoves(Chessboard chessboard, Square piecePos,
                                               Direction direction, int amount) {
        return generateMoves(chessboard, piecePos, direction.vectors, amount);
    }

    /**
     * Generates valid moves.
     *
     * @param chessboard the chessboard
     * @param piecePos   the piece position
     * @param vectors    the vectors
     * @param amount     the amount of squares the piece can move in the vector's direction
     *
     * @return all valid moves, be it normal moves, attacking moves, or castles
     */
    protected Collection<Square> generateMoves(Chessboard chessboard,
                                               Square piecePos,
                                               Pair<Integer, Integer>[] vectors,
                                               int amount) {
        Collection<Square> squares = new HashSet<>();
        if (amount <= 0 || !chessboard.containsPiece(piecePos)) {
            return squares;
        }

        final char givenPieceId = chessboard.getPieceId(piecePos);
        for (Pair<Integer, Integer> vector : vectors) {
            // check for all vectors combinations
            for (int i = 0; i < 4; i++) {
                // multiplies the vector by 0 1 0 1
                int row = piecePos.getRank() + vector.a * (i & 0b1);

                // multiplies the vector by 0 0 1 1
                int column = piecePos.getFile() + vector.b * (i & 0b10);

                int count = 0;
                // don't check for invalid positions
                // check until we reach the amount of squares the piece can move
                while (ChessPieceUtil.isValidPosition(row, column) && count++ != amount) {
                    // generate a new position
                    Square pos = new Square((byte) row, (byte) column);

                    if (chessboard.containsPiece(pos)) {
                        // a piece was found
                        char targetSquarePieceId = chessboard.getPieceId(pos);

                        // if the target square is the opponents piece, add it to the valid moves
                        if (!ChessPieceUtil.areSameColours(targetSquarePieceId, givenPieceId)) {
                            squares.add(pos);
                        }
                        break;
                    } else {
                        // no piece was found, add the position, and move in the vectors vector
                        squares.add(pos);
                        row++;
                        column++;
                    }
                }

            }
        }
        return squares;
    }

    @Override
    public Collection<Square> getAttackingSquares(Chessboard chessboard,
                                                  Square piecesSquare) {
        Collection<Square> attackingMoves = new HashSet<>();

        // the valid move collection should return both valid moves and attacking moves
        // we don't even have to check for colours here as if there's a piece on the move's
        // square that means it's an attacking move of the piece
        for (Square move : getValidMoves(chessboard, piecesSquare)) {
            if (chessboard.containsPiece(move)) {
                attackingMoves.add(move);
            }
        }

        return attackingMoves;
    }

    @Override
    public final boolean isValidMove(Chessboard chessboard, Square currentSquare,
                                     Square destination) {
        final Collection<Square> validMoves = getValidMoves(chessboard, currentSquare);
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

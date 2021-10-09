package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Position;

import java.util.Collection;
import java.util.HashSet;

/**
 * The type Abstract chess piece.
 */
abstract class AbstractChessPiece implements IChessPiece {
    private final char white;
    private final char black;

    AbstractChessPiece(char charId){
        this.white = ChessPieceUtil.toWhite(charId);
        this.black = ChessPieceUtil.toBlack(charId);
    }

    /**
     * The possible directions in vector
     */
    protected enum Direction {
        /**
         * The diagonal direction
         */
        DIAGONAL(new Pair<>(1, 1)),
        /**
         * The horizontal and vertical direction
         */
        HOR_AND_VERT(new Pair<>(0, 1), new Pair<>(1, 0));

        private final Pair<Integer, Integer>[] vectors;

        @SafeVarargs
        Direction(Pair<Integer, Integer>... vectors) {
            this.vectors = vectors;
        }
    }

    /**
     * Generates moves collection
     *
     * @param chessboard the chessboard
     * @param piecePos   the piece position
     * @param direction  the direction
     * @return the collection
     */
    protected Collection<Position> generateMoves(Chessboard chessboard, Position piecePos, Direction direction) {
        Collection<Position> positions = new HashSet<>();
        if (!chessboard.containsPiece(piecePos)) {
            return positions;
        }
        boolean white = chessboard.isWhite(piecePos);

        Position copy = new Position(piecePos);
        for (Pair<Integer, Integer> vector : direction.vectors) {
            // check for all directions
            for (int i = 0; i < 4; i++) {
                int a = vector.a * (i & 0b1);
                int b = vector.b * (i & 0b10);
            }
        }
        return positions;
    }

    @Override
    public boolean isValidMove(Chessboard chessboard, Position currentPosition, Position destination) {
        return getValidMoves(chessboard, currentPosition).contains(destination);
    }

    @Override
    public char getWhite() {
        return white;
    }

    @Override
    public char getBlack() {
        return black;
    }
}

package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Square;

import java.util.Collection;

/**
 * A chess piece.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface IChessPiece {

    /**
     * Gets all valid moves.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the current position
     *
     * @return the valid moves
     */
    Collection<Square> getValidMoves(Chessboard chessboard, Square currentSquare);

    /**
     * Gets all attacking squares of this piece. Note that this calls the
     * {@link IChessPiece#getValidMoves(Chessboard, Square)} method to get all of the valid moves.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the piece's square
     *
     * @return all attacking moves of the piece
     */
    Collection<Square> getAttackingSquares(Chessboard chessboard, Square currentSquare);

    /**
     * Checks whether the given move is valid on the chessboard
     *
     * @param chessboard    the chessboard
     * @param currentSquare the piece's current position
     * @param destination   the piece's destination position
     *
     * @return {@code true} if the move is valid
     */
    boolean isValidMove(Chessboard chessboard, Square currentSquare, Square destination);

    /**
     * @return the white colour identifier
     */
    char getWhite();

    /**
     * @return the black colour identifier
     */
    char getBlack();
}

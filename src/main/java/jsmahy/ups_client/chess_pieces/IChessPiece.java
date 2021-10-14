package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Position;

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
     * Gets valid moves.
     *
     * @param chessboard      the chessboard
     * @param currentPosition the current position
     *
     * @return the valid moves
     */
    Collection<Position> getValidMoves(Chessboard chessboard, Position currentPosition);

    /**
     * Checks whether the given move is valid on the chessboard
     *
     * @param chessboard      the chessboard
     * @param currentPosition the piece's current position
     * @param destination     the piece's destination position
     *
     * @return {@code true} if the move is valid
     */
    boolean isValidMove(Chessboard chessboard, Position currentPosition, Position destination);

    /**
     * @return the white colour identifier
     */
    char getWhite();

    /**
     * @return the black colour identifier
     */
    char getBlack();
}

package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.Position;

import java.util.Collection;

/**
 * The interface Chess piece.
 */
public interface IChessPiece {

    /**
     *
     * @return the unique id of this piece
     */
    byte getId();

    /**
     * Gets valid moves.
     *
     * @param chessboard      the chessboard
     * @param currentPosition the current position
     * @return the valid moves
     */
    Collection<Position> getValidMoves(Chessboard chessboard, Position currentPosition);
}

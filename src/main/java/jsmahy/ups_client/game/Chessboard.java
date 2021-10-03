package jsmahy.ups_client.game;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.util.Position;

import java.util.Optional;

/**
 * The type Chessboard.
 */
public class Chessboard {
    /**
     * The constant ROW_SIZE.
     */
    public static final int ROW_SIZE = 8;
    private final byte[][] board = new byte[ROW_SIZE][ROW_SIZE];
    private final boolean[][] moves = new boolean[ROW_SIZE][ROW_SIZE];

    /**
     * Sets board.
     *
     * @param board the board
     * @throws IllegalArgumentException the illegal argument exception
     */
    public void setupBoard(byte[][] board) throws IllegalArgumentException {
        if (board.length != this.board.length || board[0].length != this.board[0].length) {
            throw new IllegalArgumentException("Invalid board");
        }
        System.arraycopy(board, 0, this.board, 0, ROW_SIZE);
    }

    /**
     * Move boolean.
     *
     * @param from the from
     * @param to   the to
     * @return the boolean
     */
    public boolean move(Position from, Position to) {
        Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            return false;
        }
        IChessPiece piece = opt.get();
        if (!piece.getValidMoves(this, from).contains(to)) {
            return false;
        }
        moveOnBoard(from, to);
        return true;
    }

    private void moveOnBoard(Position from, Position to) {
        board[to.getX()][to.getY()] = board[from.getX()][from.getY()];
        board[from.getX()][from.getY()] = 0;
        moves[from.getX()][from.getY()] = true;
    }

    /**
     * Has moved boolean.
     *
     * @param at the at
     * @return the boolean
     */
    public boolean hasMoved(Position at) {
        return moves[at.getX()][at.getY()];
    }

    /**
     * Gets piece.
     *
     * @param at the at
     * @return the piece
     */
    public Optional<IChessPiece> getPiece(Position at) {
        if (!containsPiece(at)) {
            return Optional.empty();
        }
        byte id = (byte) Math.abs(getPieceId(at));

        for (ChessPieceEnum cp : ChessPieceEnum.values()) {
            if (cp.getPiece().getId() == id) {
                return Optional.of(cp.getPiece());
            }
        }
        return Optional.empty();
    }

    /**
     * Contains piece boolean.
     *
     * @param pos the pos
     * @return the boolean
     */
    public boolean containsPiece(Position pos) {
        return getPieceId(pos) != 0;
    }

    /**
     * Is white boolean.
     *
     * @param pos the pos
     * @return the boolean
     * @throws IllegalArgumentException the illegal argument exception
     */
    public boolean isWhite(Position pos) throws IllegalArgumentException {
        if (!containsPiece(pos)) {
            throw new IllegalArgumentException(String.format("No piece on %s square!", pos));
        }
        return getPieceId(pos) > 0;
    }

    private byte getPieceId(Position pos) {
        return board[pos.getX()][pos.getY()];
    }
}

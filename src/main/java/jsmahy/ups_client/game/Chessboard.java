package jsmahy.ups_client.game;

import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Position;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chessboard {
    public static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static final int ROW_SIZE = 8;

    // board of characters
    // upper case 'P' represents a white pawn, lower case 'p' represents a black pawn
    // this goes for all the pieces
    private final char[][] board = new char[ROW_SIZE][ROW_SIZE];

    /**
     * Sets up the board with the given char matrix
     *
     * @param board the board
     * @throws IllegalArgumentException
     */
    private void setupBoard(char[][] board) throws IllegalArgumentException {
        validateBoard(board);
        System.arraycopy(board, 0, this.board, 0, ROW_SIZE);
    }

    private void validateBoard(char[][] board) {
        if (board.length != this.board.length || board[0].length != this.board[0].length) {
            throw new IllegalArgumentException("Invalid board length");
        }
        String viableIds = ChessPieceUtil.getAllPieceIds();
        for (char[] cc : board) {
            for (char c : cc) {
                if (c != '\u0000' && viableIds.indexOf(c) == -1) {
                    throw new IllegalArgumentException("Invalid piece on board found: " + c);
                }
            }
        }
    }

    /**
     * Sets up a board from a fen string
     *
     * @param fen the fen string
     */
    public void setupBoard(String fen) {
        // somewhat of an ugly pattern but works
        // this just ensures the format is right
        final Pattern fenPattern =
                Pattern.compile("((([rnbqkpRNBQKP1-8]+)\\/){7}([rnbqkpRNBQKP1-8]+)) ([wb]) ([KQkq\\-]+) (([a-h][0-7])|\\-) ([0-9]+) ([0-9]+)");
        final Matcher m = fenPattern.matcher(fen);

        // very likely an invalid fen string
        if (!m.find()) {
            return;
        }

        // start parsing the string
        // the first group is the big part
        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
        String[] split = m.group(1).split("/");

        if (split.length != 8) {
            throw new IllegalStateException("Attempted to parse an invalid FEN String");
        }

        for (int i = 0; i < split.length; i++) {
            int rowIdx = 0;
            for (char c : split[i].toCharArray()) {
                // check how many row idx we skip
                if (Character.isDigit(c)) {
                    rowIdx += Character.digit(c, 10);
                } else {
                    // there could be too large of a number, and it could crash the client
                    // with an index out of bounds ex
                    if (rowIdx >= 8) {
                        break;
                    }
                    // there should be only valid characters now
                    board[i][rowIdx] = c;
                }
            }
        }
    }

    /**
     * Moves a piece on the board
     *
     * @param from the square from
     * @param to   the square to
     * @return {@code true} if a piece was moved
     */
    public ChessMove move(Position from, Position to) {
        Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            return ChessMove.NO_MOVE;
        }
        IChessPiece piece = opt.get();
        if (!piece.isValidMove(this, from, to)) {
            return ChessMove.NO_MOVE;
        }

        return moveOnBoard(from, to);
    }

    private ChessMove moveOnBoard(Position from, Position to) {
        Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            throw new IllegalStateException("No chess piece found on square " + from);
        }
        IChessPiece piece = opt.get();
        if (ChessPieceUtil.isKing(piece)) {
            // check if castles
            if (Math.abs(from.getColumn() - to.getColumn()) == 2) {

            }
        }
        board[to.getRow()][to.getColumn()] = board[from.getRow()][from.getColumn()];
        board[from.getRow()][from.getColumn()] = 0;
        return ChessMove.MOVE;
    }

    /**
     * @param square the square
     * @return an {@link Optional#of(Object)} a chess piece if there's one or {@link Optional#empty()}
     */
    public Optional<IChessPiece> getPiece(Position square) {
        if (!containsPiece(square)) {
            return Optional.empty();
        }
        return Optional.of(ChessPieceUtil.getPieceById(getPieceId(square)));
    }

    /**
     * Checks whether there's a piece on the given square
     *
     * @param pos the square
     * @return {@code true} if there's a piece
     */
    public boolean containsPiece(Position pos) {
        return getPieceId(pos) != 0;
    }

    /**
     * Checks whether the piece on the given position is white or black
     *
     * @param pos the position
     * @return {@code true} if the piece is white, {@code false} otherwise
     * @throws IllegalArgumentException if there's no piece on the position
     */
    public boolean isWhite(Position pos) throws IllegalArgumentException {
        if (!containsPiece(pos)) {
            throw new IllegalArgumentException(String.format("No piece on %s square!", pos));
        }
        return ChessPieceUtil.isWhite(getPieceId(pos));
    }

    private char getPieceId(Position pos) {
        return board[pos.getRow()][pos.getColumn()];
    }

}

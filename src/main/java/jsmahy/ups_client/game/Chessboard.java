package jsmahy.ups_client.game;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.exception.InvalidFENFormatException;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Position;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Matcher;

import static java.lang.String.format;

public class Chessboard {

    private static final Logger L = LogManager.getLogger(Chessboard.class);
    // board of characters
    // upper case 'P' represents a white pawn, lower case 'p' represents a black pawn
    // this goes for all the pieces
    private final char[][] board = new char[ChessPieceUtil.ROW_SIZE][ChessPieceUtil.ROW_SIZE];
    // 0 = white | short
    // 1 = white | long
    // 2 = black | short
    // 3 = black | long
    private final boolean[] allowedCastles = new boolean[4];

    /**
     * Sets up the board to the default position once initialized.
     */
    public Chessboard() {
        Arrays.fill(allowedCastles, true);
    }

    /**
     * Sets up a board from a fen string.
     *
     * @param fen the fen string
     *
     * @throws IllegalArgumentException if the fen string is invalid
     */
    public void setupBoard(String fen) throws IllegalArgumentException {
        // somewhat of an ugly pattern but works
        // this just ensures the format is right
        final Matcher m = Util.FEN_PATTERN.matcher(fen);

        // very likely an invalid fen string
        if (!m.find()) {
            throw new IllegalArgumentException(format("Invalid FEN String %s", fen));
        }

        // start parsing the string
        // the first group is the big part
        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
        final String[] split = m.group(1).split("/");
        L.trace(format("First part of FEN: %s", Arrays.toString(split)));
        if (split.length != 8) {
            throw new InvalidFENFormatException("Attempted to parse an invalid FEN String");
        }

        for (int i = 0; i < split.length; i++) {
            int rowIdx = 0;
            for (char c : split[i].toCharArray()) {
                // check how many row idx we skip
                if (Character.isDigit(c)) {
                    rowIdx += Character.digit(c, 10);
                } else {
                    // there could be too large of a number
                    // or the piece id is nonexistent
                    if (rowIdx >= 8 || !ChessPieceUtil.isPiece(c)) {
                        throw new InvalidFENFormatException(
                                "Attempted to parse an invalid FEN String");
                    }
                    // there should be only valid characters now
                    board[i][rowIdx++] = c;
                }
            }
        }
        final int rowSize = ChessPieceUtil.ROW_SIZE;
        L.trace("Chessboard after first FEN part:");
        for (int i = rowSize - 1; i >= 0; i--) {
            char[] bbuf = new char[8];
            for (int j = 0; j < rowSize; j++) {
                char c = board[i][j];
                if (c == '\u0000') {
                    c = ' ';
                }
                bbuf[j] = c;
            }
            L.trace(Arrays.toString(bbuf));
        }
        L.trace("Chessboard after first FEN part: " + Arrays.deepToString(board));
        // end of chessboard piece parsing

    }

    public void modifyCastlesPrivilege(boolean white, boolean shortCastles, boolean allow) {
        allowedCastles[getCastlesIndex(white, shortCastles)] = allow;
    }

    private void updateCastlesPrivileges(Position from) {
        Optional<IChessPiece> piece = getPiece(from);
        piece.ifPresent(x -> {
            boolean isWhite = isWhite(from);

            // check for king move
            if (ChessPieceUtil.isKing(x)) {
                modifyCastlesPrivilege(isWhite, true, false);
                modifyCastlesPrivilege(isWhite, false, false);
            }
            // and a rook move as well
            else if (ChessPieceUtil.is(x, ChessPieceEnum.ROOK)) {
                // if the rook is at the right edge (7) that means it's short castles
                // else it means it's long castles
                modifyCastlesPrivilege(isWhite, from.getColumn() == 7, false);
            }
        });
    }

    public boolean canKingCastles(boolean white) {
        return getAllowedCastles(white, true) && getAllowedCastles(white, false);
    }

    public boolean getAllowedCastles(boolean white, boolean shortCastles) {
        return allowedCastles[getCastlesIndex(white, shortCastles)];
    }

    private int getCastlesIndex(boolean white, boolean shortCastles) {
        int idx = white ? 0 : 2;
        idx += shortCastles ? 0 : 1;
        return idx;
    }

    /**
     * Moves a piece on the board.
     *
     * @param from the square from
     * @param to   the square to
     *
     * @return the chess move that was performed
     */
    public ChessMove move(Position from, Position to, ChessPlayer as) {
        Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            return ChessMove.NO_MOVE;
        }
        IChessPiece piece = opt.get();
        if (!piece.isValidMove(this, from, to)) {
            return ChessMove.NO_MOVE;
        }

        return moveOnBoard(from, to, as);
    }

    /**
     * Moves the piece on the board after all the validation is done
     *
     * @param from the starting pos
     * @param to   the ending pos
     *
     * @return a chess move type
     *
     * @throws IllegalStateException if there's no piece on the starting position
     * @throws IllegalAccessError    if the client attempted to move opponents piece and the
     *                               client called this method for some reason
     */
    private ChessMove moveOnBoard(Position from, Position to, ChessPlayer as)
            throws IllegalStateException {
        final Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            throw new IllegalStateException("No chess piece found on square " + from);
        }
        final IChessPiece piece = opt.get();

        // check if the player was actually holding his piece
        if (as.isWhite() ^ isWhite(from)) {
            throw new IllegalAccessError("Attempted to move a piece that was the incorrect " +
                    "colour!");
        }

        ChessMove chessMove = ChessMove.MOVE;

        if (ChessPieceUtil.isKing(piece)) {
            Collection<Position> opponentsAttackingSquares = getOpponentsAttackingSquares();
            chessMove = checkIfCastles(from, to, chessMove);
        }

        // update the privileges if the piece on the "from" position was a rook or a king
        updateCastlesPrivileges(from);
        board[to.getRow()][to.getColumn()] = board[from.getRow()][from.getColumn()];
        board[from.getRow()][from.getColumn()] = 0;
        return chessMove;
    }

    private Collection<Position> getOpponentsAttackingSquares() {
        Collection<Position> c = new HashSet<>();
        return null;
    }

    private ChessMove checkIfCastles(final Position from, final Position to, ChessMove chessMove) {
        // check for castles
        final int posAmount = Math.abs(from.getColumn() - to.getColumn());
        if (posAmount == 2 && getAllowedCastles(isWhite(from), true)) {
            chessMove = ChessMove.CASTLES_SHORT;
        } else if (posAmount == 3 && getAllowedCastles(isWhite(from), false)) {
            chessMove = ChessMove.CASTLES_LONG;
        }
        return chessMove;
    }

    /**
     * @param square the square
     *
     * @return an {@link Optional#of(Object)} a chess piece if there's one or
     * {@link Optional#empty()}
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
     *
     * @return {@code true} if there's a piece
     */
    public boolean containsPiece(Position pos) {
        return getPieceId(pos) != 0;
    }

    /**
     * Checks whether the piece on the given position is white or black
     *
     * @param pos the position
     *
     * @return {@code true} if the piece is white, {@code false} otherwise
     *
     * @throws IllegalArgumentException if there's no piece on the position
     */
    public boolean isWhite(Position pos) throws IllegalArgumentException {
        if (!containsPiece(pos)) {
            throw new IllegalArgumentException(format("No piece on %s square!", pos));
        }
        return ChessPieceUtil.isWhite(getPieceId(pos));
    }

    public char getPieceId(Position pos) throws IllegalArgumentException {
        final char c = board[pos.getRow()][pos.getColumn()];
        if (!ChessPieceUtil.isPiece(c)) {
            throw new IllegalArgumentException("No piece found on position " + pos);
        }
        return c;
    }

}

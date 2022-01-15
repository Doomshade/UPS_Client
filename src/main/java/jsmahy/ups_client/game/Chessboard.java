package jsmahy.ups_client.game;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.exception.InvalidFENFormatException;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Square;
import jsmahy.ups_client.util.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Matcher;

import static java.lang.String.format;

public final class Chessboard {

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
     * Instantiates a new chessboard
     */
    public Chessboard() {
        Arrays.fill(allowedCastles, true);
    }

    /**
     * Sets up a board from a fen string.
     *
     * @param fen the fen string
     * @throws InvalidFENFormatException if the fen string is invalid
     */
    public void setupBoard(String fen) throws InvalidFENFormatException {
        // somewhat of an ugly pattern but works
        // this just ensures the format is right
        final Matcher m = Util.FEN_PATTERN.matcher(fen);

        // very likely an invalid fen string
        if (!m.find()) {
            throw new InvalidFENFormatException(format("Invalid FEN String %s", fen));
        }

        // start parsing the string
        // the first group is the big part
        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
        final String[] split = m.group(1).split("/");
        L.trace(format("First part of FEN: %s", Arrays.toString(split)));
        if (split.length != 8) {
            throw new InvalidFENFormatException("Attempted to parse an invalid FEN String");
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = ' ';
            }

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
        for (int i = rowSize - 1; i >= 0; i--) {
            char[] bbuf = new char[8];
            for (int j = 0; j < rowSize; j++) {
                char c = board[i][j];
                if (c == '\u0000') {
                    c = ' ';
                }
                bbuf[j] = c;
            }
        }
        L.trace("Chessboard after first FEN part: " + Arrays.deepToString(board));
        // end of chessboard piece parsing

        L.info("Successfully set up chessboard. Board:\n" + Arrays.deepToString(board));
    }

    public void modifyCastlesPrivilege(boolean white, boolean shortCastles, boolean allow) {
        allowedCastles[getCastlesIndex(white, shortCastles)] = allow;
        L.trace(format("Modified castles privileges for %s on %s to %s", white ? "white" : "black",
                shortCastles ? "short castles" : "long castles", allow));
    }

    private void updateCastlesPrivileges(Square from) {
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
                modifyCastlesPrivilege(isWhite, from.getFile() == 7, false);
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
     * @return the chess move that was performed
     */
    public ChessMove move(Square from, Square to, ChessPlayer as) {
        Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            return ChessMove.NO_MOVE;
        }
        IChessPiece piece = opt.get();
        if (!piece.isValidMove(this, from, to)) {
            return ChessMove.NO_MOVE;
        }

        // check if the player was actually holding his piece
        if (as.isWhite() ^ isWhite(from)) {
            return ChessMove.NO_MOVE;
        }
        return moveOnBoard(from, to);
    }

    /**
     * Moves the piece on the board after all the validation is done
     *
     * @param from the starting pos
     * @param to   the ending pos
     * @return a chess move type
     * @throws IllegalStateException if there's no piece on the starting position
     * @throws IllegalAccessError    if the client attempted to move opponents piece and the
     *                               client called this method for some reason
     */
    public ChessMove moveOnBoard(Square from, Square to)
            throws IllegalStateException {
        final Optional<IChessPiece> opt = getPiece(from);
        if (opt.isEmpty()) {
            throw new IllegalStateException("No chess piece found on square " + from);
        }
        final IChessPiece piece = opt.get();

        ChessMove chessMove = ChessMove.MOVE;

        if (ChessPieceUtil.isKing(piece)) {
            chessMove = checkIfCastles(from, to, chessMove);
        }

        // update the privileges if the piece on the "from" position was a rook or a king
        updateCastlesPrivileges(from);

        L.trace(format("Moving from %s to %s", from, to));
        board[to.getRank()][to.getFile()] = board[from.getRank()][from.getFile()];
        board[from.getRank()][from.getFile()] = 0;
        return chessMove;
    }

    /**
     * Checks whether there are opponent's pieces attacking the squares
     *
     * @param kingsPos the king's position
     * @param to       the king's destination
     * @return {@code true} if the two squares next to the king based on the castle type are not
     * under attack of the opponent
     */
    private boolean isOpponentAttackingKingsSquares(Square kingsPos, Square to) {
        final boolean white = isWhite(kingsPos);
        final Collection<Square> attackingMoves = new HashSet<>();

        for (int rank = 0; rank < ChessPieceUtil.ROW_SIZE; rank++) {
            for (int file = 0; file < ChessPieceUtil.ROW_SIZE; file++) {
                final Square s = new Square(rank, file);

                // the piece and the opponent's colour matches ->
                // it's the opponent's piece ->
                // add the attacking squares of the piece
                if (isWhite(s) == white) {
                    getPiece(s).ifPresent(x ->
                            attackingMoves.addAll(x.getAttackingSquares(Chessboard.this, s)));
                }
            }
        }

        final int sgn = (int) Math.signum(kingsPos.getRank() - to.getRank());
        // return true if the attacking moves don't contain the two squares next to the king in the
        // direction of the castles
        return !attackingMoves.contains(kingsPos.add(1 * sgn, 0)) &&
                !attackingMoves.contains(kingsPos.add(2 * sgn, 0));
    }

    /**
     * Checks whether the move was a castles move from the king
     *
     * @param from      the king's source position
     * @param to        the king's destination
     * @param chessMove the current chess move
     * @return {@link ChessMove#CASTLES_SHORT} or {@link ChessMove#CASTLES_LONG} or chessMove if
     * the move was either not a castles move or the king cannot castle
     */
    private ChessMove checkIfCastles(final Square from, final Square to, ChessMove chessMove) {
        // first check whether the squares are not under attack
        if (!isOpponentAttackingKingsSquares(from, to)) {
            return chessMove;
        }
        // then check for both castles individually
        final int posAmount = Math.abs(from.getFile() - to.getFile());

        // check for short castles
        if (posAmount == 2 && getAllowedCastles(isWhite(from), true)) {
            chessMove = ChessMove.CASTLES_SHORT;
        }

        // check for long castles
        else if (posAmount == 3 && getAllowedCastles(isWhite(from), false)) {
            chessMove = ChessMove.CASTLES_LONG;
        }

        return chessMove;
    }

    /**
     * @param square the square
     * @return an {@link Optional#of(Object)} a chess piece if there's one or
     * {@link Optional#empty()}
     */
    public Optional<IChessPiece> getPiece(Square square) {
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
    public boolean containsPiece(Square pos) {
        try {
            // if there is a piece, the pieceId will not throw an ex
            getPieceId(pos);
            return true;
        } catch (IllegalArgumentException e) {
            // if there is no piece the getPieceId throws an IllegalArgumentException
            return false;
        }
    }

    /**
     * Checks whether the piece on the given position is white or black
     *
     * @param pos the position
     * @return {@code true} if the piece is white, {@code false} otherwise
     * @throws IllegalArgumentException if there's no piece on the position
     */
    public boolean isWhite(Square pos) throws IllegalArgumentException {
        if (!containsPiece(pos)) {
            throw new IllegalArgumentException(format("No piece on %s square!", pos));
        }
        return ChessPieceUtil.isWhite(getPieceId(pos));
    }

    /**
     * Returns the piece ID on the given square.
     *
     * @param pos the square to look for
     * @return the piece ID
     * @throws IllegalArgumentException if there is no piece on the square
     */
    public char getPieceId(Square pos) throws IllegalArgumentException {
        final char c = board[pos.getRank()][pos.getFile()];
        if (!ChessPieceUtil.isPiece(c)) {
            throw new IllegalArgumentException("No piece found on square " + pos);
        }
        return c;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("board", board)
                .append("allowedCastles", allowedCastles)
                .toString();
    }
}

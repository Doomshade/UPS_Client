package jsmahy.ups_client.game;

import jsmahy.ups_client.chess_pieces.ChessPieceEnum;
import jsmahy.ups_client.chess_pieces.IChessPiece;
import jsmahy.ups_client.net.Player;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Position;

import java.util.Arrays;
import java.util.Optional;

/**
 * The type Chess game.
 */
public final class ChessGame {
    private final Chessboard chessboard;
    private final Player white;
    private final Player black;

    // 0 = white | short
    // 1 = white | long
    // 2 = black | short
    // 3 = black | long
    private final boolean[] allowedCastles = new boolean[4];

    /**
     * Instantiates a new Chess game.
     *
     * @param chessboard the chessboard
     * @param white      the white
     * @param black      the black
     */
    public ChessGame(Chessboard chessboard, Player white, Player black) {
        this.chessboard = chessboard;
        this.white = white;
        this.black = black;
        Arrays.fill(allowedCastles, true);
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public Player getWhite() {
        return white;
    }

    public Player getBlack() {
        return black;
    }

    /**
     * Moves a piece and propagates the move to the server
     *
     * @param from the from
     * @param to   the to
     */
    public void movePiece(Position from, Position to) {
        if (chessboard.move(from, to) != ChessMove.NO_MOVE) {
            //
            updateCastlesPrivileges(from);
            // send packet to players
        }
    }

    private void updateCastlesPrivileges(Position from) {
        Optional<IChessPiece> piece = chessboard.getPiece(from);
        piece.ifPresent(x -> {
            boolean isWhite = chessboard.isWhite(from);

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

    public void modifyCastlesPrivilege(boolean white, boolean shortCastles, boolean allow) {
        allowedCastles[getCastlesIndex(white, shortCastles)] = allow;
    }

    private int getCastlesIndex(boolean white, boolean shortCastles) {
        int idx = white ? 0 : 2;
        idx += shortCastles ? 0 : 1;
        return idx;
    }

    public boolean getAllowedCastles(boolean white, boolean shortCastles) {
        return allowedCastles[getCastlesIndex(white, shortCastles)];
    }

    public boolean canKingCastles(boolean white) {
        return getAllowedCastles(white, true) && getAllowedCastles(white, false);
    }
}

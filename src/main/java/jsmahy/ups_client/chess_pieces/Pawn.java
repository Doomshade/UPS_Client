package jsmahy.ups_client.chess_pieces;

import jsmahy.ups_client.Main;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.util.ChessPieceUtil;
import jsmahy.ups_client.util.Pair;
import jsmahy.ups_client.util.Square;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;

class Pawn extends AbstractChessPiece {
    private static final transient Logger L = LogManager.getLogger(Pawn.class);

    Pawn() {
        super('p');
    }

    @Override
    public Collection<Square> getValidMoves(Chessboard chessboard, Square currentSquare) {
        Collection<Square> validMoves = new HashSet<>();

        addMoves(chessboard, currentSquare, validMoves);
        addAttackingMoves(chessboard, currentSquare, validMoves);
        return validMoves;
    }

    /**
     * Adds attacking moves of the pawn to the collection.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the position of the pawn
     * @param validMoves    the collection
     */
    private void addAttackingMoves(Chessboard chessboard, Square currentSquare,
                                   Collection<Square> validMoves) {
        // TODO add en passant
        int direction = getDirection(chessboard, currentSquare);
        Square left = currentSquare.add(direction, -1);
        Square right = currentSquare.add(direction, 1);

        addAttackingMove(chessboard, currentSquare, validMoves, left);
        addAttackingMove(chessboard, currentSquare, validMoves, right);
    }

    /**
     * Adds an attacking move of the pawn to the collection.
     *
     * @param chessboard     the chessboard
     * @param currentSquare  the position of the pawn
     * @param validMoves     the collection
     * @param attackedSquare the attacked square to add
     */
    private void addAttackingMove(final Chessboard chessboard, final Square currentSquare,
                                  final Collection<Square> validMoves,
                                  final Square attackedSquare) {
        char attackedPieceId;
        try {
            attackedPieceId = chessboard.getPieceId(attackedSquare);
        } catch (IllegalArgumentException e) {
            // no piece on the attacked square
            return;
        }
        L.debug(String.format("Found %c piece on square %s, adding to attacking moves",
                attackedPieceId, attackedSquare));
        // check whether it's an opposite coloured piece
        if (!ChessPieceUtil.areSameColours(chessboard.getPieceId(currentSquare),
                attackedPieceId)) {
            validMoves.add(attackedSquare);
        }
    }

    /**
     * Adds the valid moves to the collection.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the current position of the pawn
     * @param validMoves    the collection
     */
    private void addMoves(Chessboard chessboard, Square currentSquare,
                          Collection<Square> validMoves) {

        // if the piece is white the pawn moves up (+1), black moves down (-1)
        int direction = getDirection(chessboard, currentSquare);
        L.debug(String.format("Adding pieces for %s in %s direction", currentSquare,
                direction > 0 ? "upwards" : "downwards"));
        // check if there's a piece one square up
        // if there is, don't add moves
        final Square add = currentSquare.add(direction, 0);
        if (chessboard.containsPiece(add)) {
            L.debug(String.format("Found %c piece on square %s, no moves added",
                    chessboard.getPieceId(add), add));
            return;
        }

        // check for the two-square move
        if (canMoveTwoSquares(chessboard, currentSquare)) {
            L.debug("Can move by two squares, adding it to valid moves");
            byte by = (byte) (2 * getDirection(chessboard, currentSquare));
            validMoves.add(currentSquare.add(by, 0));
        }

        // move just by one row
        Pair[] vectors = {new Pair(direction, 0)};
        Collection<Square> c = generateMoves(chessboard, currentSquare,
                // cast it to the generic argument so the method actually returns a collection
                // with a generic argument
                // java in a nutshell
                (Pair<Integer, Integer>[]) vectors, 1);
        validMoves.addAll(c);
    }

    /**
     * Gets the pawn's moving direction based on its colour.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the current position of the pawn
     *
     * @return {@code 1} if the pawn is white or {@code -1} if the pawn is black
     *
     * @throws IllegalArgumentException if there's no piece on the given position
     */
    private int getDirection(final Chessboard chessboard, final Square currentSquare)
            throws IllegalArgumentException {
        return chessboard.isWhite(currentSquare) ? 1 : -1;
    }

    /**
     * Checks whether the piece has the privilege to move by two squares. Note that this does
     * not
     * check whether there's a piece in front of the piece on the currentPosition.
     *
     * @param chessboard    the chessboard
     * @param currentSquare the current position
     *
     * @return {@code true} if the pawn is on second rank and white or seventh rank and black
     */
    private boolean canMoveTwoSquares(Chessboard chessboard, Square currentSquare)
            throws IllegalArgumentException {

        // is white and 2nd rank (row)
        if (chessboard.isWhite(currentSquare) && currentSquare.getRank() == 1) {
            return true;
        }

        // is black and 7th rank (row)
        return !chessboard.isWhite(currentSquare) && currentSquare.getRank() == 6;
    }
}

package chess.domain.pieces;

import chess.domain.board.Player;

/**
 *
 * This class corresponds physical bishop-piece thus only containing knowledge
 * of it's place, whether or not is has moved yet and if it moved two squares
 * last turn. Class also offers methods to access this information and clone
 * this pawn.
 *
 * @author sami
 */
public class Pawn extends Piece {

    /**
     * Boolean that tells if this pawn did move two squares last turn.
     */
    private boolean movedTwoSquaresLastTurn;
    /**
     * Boolean which keeps track of whether or not this pawn has been moved.
     */
    private boolean hasBeenMoved;

    /**
     * Creates a new Pawn-class object with given location and owner.
     *
     * @param column column of the square this pawn is placed on
     * @param row row of the square this pawn is placed on
     * @param owner owner of this pawn
     * @param pieceCode pieceCode of this piece
     */
    public Pawn(int column, int row, Player owner, String pieceCode) {
        super(column, row, owner, pieceCode);
        movedTwoSquaresLastTurn = false;
        hasBeenMoved = false;
    }

    @Override
    public void makeDeeplyEqualTo(Piece piece) {
//        if (piece == null || piece.getClass() != Pawn.class) {
//            return;
//        }

        Pawn pawn = (Pawn) piece;
        this.hasBeenMoved = pawn.getHasBeenMoved();
        this.movedTwoSquaresLastTurn = pawn.getMovedTwoSquaresLastTurn();
        super.makeDeeplyEqualTo(piece);
    }

    public boolean getHasBeenMoved() {
        return this.hasBeenMoved;
    }

    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }

    public boolean getMovedTwoSquaresLastTurn() {
        return movedTwoSquaresLastTurn;
    }

    public void setMovedTwoSquaresLastTurn(boolean movedTwoSquaresLastTurn) {
        this.movedTwoSquaresLastTurn = movedTwoSquaresLastTurn;
    }

    @Override
    public Piece clone() {
        Pawn clone = new Pawn(column, row, owner, pieceCode);
        clone.setHasBeenMoved(hasBeenMoved);
        clone.setMovedTwoSquaresLastTurn(movedTwoSquaresLastTurn);
        clone.setTaken(taken);
        return clone;
    }

    /**
     * Returns last row in the direction this pawn is moving towards.
     *
     * @return integer value of last row on board in direction of movement.
     */
    public int opposingEnd() {
        if (owner == Player.BLACK) {
            return 7;
        }
        return 0;
    }
}

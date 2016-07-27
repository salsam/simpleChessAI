package chess.domain.pieces;

import chess.domain.board.Player;

/**
 *
 * This class corresponds physical rook-piece thus only containing knowledge of
 * it's place and whether or not it has been moved so far. This class gives
 * methods to access that data and also to clone this rook.
 *
 * @author sami
 */
public class Rook extends Piece {

    /**
     * Boolean that keeps track of whether or not this rook has been moved.
     */
    private boolean hasBeenMoved;

    /**
     * Creates a new Rook-class object with given location and owner.
     *
     * @param column column of the square this rook will be placed on
     * @param row row of the square this rook will be placed on
     * @param player owner of this rook
     * @param pieceCode pieceCode of this piece
     */
    public Rook(int column, int row, Player player, String pieceCode) {
        super(column, row, player, pieceCode);
        this.hasBeenMoved = false;
    }

    @Override
    public void makeDeeplyEqualTo(Piece piece) {
        if (piece.getClass() != Rook.class) {
            return;
        }
        Rook rook = (Rook) piece;
        this.hasBeenMoved = rook.getHasBeenMoved();
        super.makeDeeplyEqualTo(piece);
    }

    public boolean getHasBeenMoved() {
        return hasBeenMoved;
    }

    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }

    @Override
    public Piece clone() {
        Rook clone = new Rook(column, row, owner, pieceCode);
        clone.setHasBeenMoved(hasBeenMoved);
        clone.setTaken(taken);
        return clone;
    }
}

package chess.domain.pieces;

import chess.domain.board.Player;

/**
 *
 * This class corresponds physical king-piece thus only containing knowledge of
 * it's place and whether or not is has been moved. This class also offers a
 * method to clone this king.
 *
 * @author sami
 */
public class King extends Piece {

    /**
     * Boolean that keeps track of whether or not this king has been moved yet.
     */
    private boolean hasBeenMoved;

    /**
     * Creates a new King-object in given location with given owner.
     *
     * @param column column of the square this king will be placed on
     * @param row row of the square this king will be placed on
     * @param player owner of this king
     * @param pieceCode pieceCode of this piece
     */
    public King(int column, int row, Player player, String pieceCode) {
        super(column, row, player, pieceCode);
        this.hasBeenMoved = false;
    }

    @Override
    public void makeDeeplyEqualTo(Piece piece) {
        if (piece.getClass() != King.class) {
            return;
        }
        King king = (King) piece;
        super.makeDeeplyEqualTo(piece);
        this.hasBeenMoved = king.getHasBeenMoved();
    }

    public boolean getHasBeenMoved() {
        return hasBeenMoved;
    }

    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }

    @Override
    public Piece clone() {
        King clone = new King(column, row, owner, pieceCode);
        clone.setHasBeenMoved(hasBeenMoved);
        return clone;
    }

}

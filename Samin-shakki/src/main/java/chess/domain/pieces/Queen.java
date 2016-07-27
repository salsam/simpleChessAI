package chess.domain.pieces;

import chess.domain.board.Player;

/**
 *
 * This class corresponds physical queen-piece thus only containing knowledge of
 * it's place and a method to copy it.
 *
 * @author sami
 */
public class Queen extends Piece {

    /**
     * Creates a new Queen-class object with given location and owner.
     *
     * @param column column of the square this queen is placed on
     * @param row row of the square this queen is placed on
     * @param player owner of this queen
     * @param pieceCode pieceCode of this piece
     */
    public Queen(int column, int row, Player player, String pieceCode) {
        super(column, row, player, pieceCode);
    }

    @Override
    public Piece clone() {
        Queen clone = new Queen(column, row, owner, pieceCode);
        clone.setTaken(taken);
        return clone;
    }

}

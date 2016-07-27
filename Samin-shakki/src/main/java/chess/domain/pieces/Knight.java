package chess.domain.pieces;

import chess.domain.board.Player;

/**
 *
 * This class corresponds physical knight-piece thus only containing knowledge
 * of it's place and a method to copy it.
 *
 * @author sami
 */
public class Knight extends Piece {

    /**
     * Creates a new Knight-class object with given location and owner.
     *
     * @param column column of the square this knight will be placed on
     * @param row row of the square this knight will be placed on
     * @param player owner of this knight
     * @param pieceCode pieceCode of this piece
     */
    public Knight(int column, int row, Player player, String pieceCode) {
        super(column, row, player, pieceCode);
    }

    @Override
    public Piece clone() {
        Knight clone = new Knight(column, row, owner, pieceCode);
        clone.setTaken(taken);
        return clone;
    }

}

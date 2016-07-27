package chess.domain.pieces;

import chess.domain.board.Player;

/**
 * This class corresponds physical bishop-piece thus only containing knowledge
 * of it's place and a method to copy it.
 *
 * @author sami
 */
public class Bishop extends Piece {

    /**
     * Creates a new Bishop-object with give owner and location.
     *
     * @param column column this bishop will be placed on
     * @param row row this bishop will be placed on
     * @param owner owner of this bishop
     * @param pieceCode pieceCode of this piece
     */
    public Bishop(int column, int row, Player owner, String pieceCode) {
        super(column, row, owner, pieceCode);
    }

    @Override
    public Piece clone() {
        Bishop clone = new Bishop(column, row, owner, pieceCode);
        clone.setTaken(taken);
        return clone;
    }

}

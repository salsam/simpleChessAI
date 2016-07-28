package chess.domain;

import chess.domain.board.Square;
import chess.domain.pieces.Piece;

/**
 *
 * @author sami
 */
public class Move {

    private Piece piece;
    private Square target;

    public Move(Piece piece, Square target) {
        this.piece = piece;
        this.target = target;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Square getTarget() {
        return target;
    }

    public void setTarget(Square target) {
        this.target = target;
    }

}

package chess.domain.pieces;

import chess.domain.board.Player;
import static chess.domain.board.Player.BLACK;
import java.util.Objects;

/**
 *
 * @author sami
 */
public class BetterPiece extends Piece {

    private Klass klass;
    private boolean hasBeenMoved;
    private boolean movedTwoSquaresLastTurn;

    public BetterPiece(Klass klass, int column, int row, Player owner, String pieceCode) {
        super(column, row, owner, pieceCode);
        this.klass = klass;
    }

    public Klass getKlass() {
        return klass;
    }

    public void setKlass(Klass klass) {
        this.klass = klass;
    }

    public boolean isHasBeenMoved() {
        return hasBeenMoved;
    }

    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }

    public boolean isMovedTwoSquaresLastTurn() {
        return movedTwoSquaresLastTurn;
    }

    public void setMovedTwoSquaresLastTurn(boolean movedTwoSquaresLastTurn) {
        this.movedTwoSquaresLastTurn = movedTwoSquaresLastTurn;
    }

    public boolean deepEquals(BetterPiece other) {
        if (other == null) {
            return false;
        }
        if (!this.pieceCode.equals(other.getPieceCode())) {
            return false;
        }

        if (this.klass != other.getKlass()) {
            return false;
        }

        if (this.column != other.getColumn()) {
            return false;
        }
        if (this.row != other.getRow()) {
            return false;
        }

        if (this.owner != other.getOwner()) {
            return false;
        }

        if (this.taken != other.isTaken()) {
            return false;
        }

        if (this.hasBeenMoved != other.isHasBeenMoved()) {
            return false;
        }

        return this.movedTwoSquaresLastTurn == other.isMovedTwoSquaresLastTurn();
    }

    public void makeDeeplyEqualTo(BetterPiece piece) {
        klass = piece.getKlass();
        column = piece.getColumn();
        row = piece.getRow();
        owner = piece.getOwner();
        taken = piece.isTaken();
        hasBeenMoved = piece.isHasBeenMoved();
        movedTwoSquaresLastTurn = piece.isMovedTwoSquaresLastTurn();
        pieceCode = piece.getPieceCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.pieceCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BetterPiece other = (BetterPiece) obj;
        if (!Objects.equals(this.pieceCode, other.pieceCode)) {
            return false;
        }
        return true;
    }

    public BetterPiece clone() {
        BetterPiece ret = new BetterPiece(klass, column, row, owner, pieceCode);
        ret.setTaken(taken);
        ret.setHasBeenMoved(hasBeenMoved);
        ret.setMovedTwoSquaresLastTurn(movedTwoSquaresLastTurn);
        return ret;
    }

    public boolean isAtOpposingEnd() {
        if (owner == BLACK) {
            return row == 7;
        }
        return row == 0;
    }

    @Override
    public String toString() {
        return "piececode: " + pieceCode + " class: " + klass + " location: ("
                + column + "," + row + ") owner: " + owner + " taken: " + taken
                + " hasbeenmoved: " + hasBeenMoved + " moved2squares: " + movedTwoSquaresLastTurn;
    }

}

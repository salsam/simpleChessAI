package chess.domain.datastructures;

import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import java.util.Objects;

/**
 * This class is used to save quadruples that will be used as keys in
 * transposition tables.
 *
 * @author sami
 */
public class TranspositionKey {

    private int height;
    private Player whoseTurn;
    private long hashedBoard;

    public TranspositionKey(int height, Player whoseTurn, long hash) {
        this.height = height;
        this.whoseTurn = whoseTurn;
        this.hashedBoard = hash;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int first) {
        this.height = first;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(Player second) {
        this.whoseTurn = second;
    }

    public long getHashedBoard() {
        return hashedBoard;
    }

    public void setHashedBoard(long hashedBoard) {
        this.hashedBoard = hashedBoard;
    }

    public TranspositionKey opposingKey() {
        return new TranspositionKey(height, getOpponent(whoseTurn), hashedBoard);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.height;
        hash = 47 * hash + Objects.hashCode(this.whoseTurn);
        hash = 47 * hash + (int) (this.hashedBoard ^ (this.hashedBoard >>> 32));
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
        final TranspositionKey other = (TranspositionKey) obj;
        if (this.height != other.height) {
            return false;
        }
        if (this.hashedBoard != other.hashedBoard) {
            return false;
        }
        if (this.whoseTurn != other.whoseTurn) {
            return false;
        }
        return true;
    }

}

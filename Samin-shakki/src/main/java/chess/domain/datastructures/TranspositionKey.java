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

    private int depth;
    private Player whoseTurn;
    private Player player;
    private long hashedBoard;

    public TranspositionKey(int depth, Player whoseTurn, Player player, long hash) {
        this.depth = depth;
        this.whoseTurn = whoseTurn;
        this.player = player;
        this.hashedBoard = hash;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int first) {
        this.depth = first;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(Player second) {
        this.whoseTurn = second;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player third) {
        this.player = third;
    }

    public long getHashedBoard() {
        return hashedBoard;
    }

    public void setHashedBoard(long hashedBoard) {
        this.hashedBoard = hashedBoard;
    }

    public TranspositionKey opposingKey() {
        TranspositionKey ret = new TranspositionKey(
                depth, whoseTurn, getOpponent(player), hashedBoard);
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.depth;
        hash = 47 * hash + Objects.hashCode(this.whoseTurn);
        hash = 47 * hash + Objects.hashCode(this.player);
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
        if (this.depth != other.depth) {
            return false;
        }
        if (this.hashedBoard != other.hashedBoard) {
            return false;
        }
        if (this.whoseTurn != other.whoseTurn) {
            return false;
        }
        if (this.player != other.player) {
            return false;
        }
        return true;
    }

}

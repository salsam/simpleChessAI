package chess.logic.ailogic;

import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.domain.pieces.*;
import java.util.Random;
import java.util.Set;

/**
 * This class is used to hash chessboard situations by Zobrist-hashing. Hashes
 * will then be used for transformation tables to speed up negamax.
 *
 * @author sami
 */
public class ZobristHasher {

    private long[][] squareHashes;

    /**
     * Creates a new ZobristHasher-object giving each square+piece combination a
     * random long value. Assumes no same hashes were received as chance is
     * miniscule. 14 different kinds of pieces can be on each square as empty
     * square and king that can castle vs king that can't are their own
     * alternatives. Indices for different pieces are 0 for empty and 13 for
     * king that can castle. Then 1-6 for white and 7-12 for black pieces. 1 is
     * pawn, 2 is rook, 3 is knight, 4 is bishop, 5 is queen and 6 is king.
     */
    public ZobristHasher() {
        squareHashes = new long[64][14];
        Random random = new Random();
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 14; j++) {
                squareHashes[i][j] = random.nextLong();
            }
        }
    }

    /**
     * Returns index associated with piece that square contains. This index can
     * then be used to check hash of given square on given board.
     *
     * @param board chessboard that square is on.
     * @param sq square of which piece is being checked.
     * @return index of piece placed on this square.
     */
    private int numberOfPiece(ChessBoard board, Square sq) {
        if (!sq.containsAPiece()) {
            return 0;
        }
        int ret = 0;

        if (sq.getPiece().getOwner() == Player.BLACK) {
            ret += 6;
        }

        if (sq.getPiece().getClass() == Pawn.class) {
            ret += 1;
        } else if (sq.getPiece().getClass() == Rook.class) {
            ret += 2;
        } else if (sq.getPiece().getClass() == Knight.class) {
            ret += 3;
        } else if (sq.getPiece().getClass() == Bishop.class) {
            ret += 4;
        } else if (sq.getPiece().getClass() == Queen.class) {
            ret += 5;
        } else if (kingCanCastle(board, sq)) {
            ret = 13;
        } else {
            ret += 6;
        }

        return ret;

    }

    private boolean kingCanCastle(ChessBoard board, Square sq) {
        Set kingPossibleMoves = board.getMovementLogic().possibleMoves(sq.getPiece(), board);

        if (sq.getColumn() > 1
                && kingPossibleMoves.contains(board.getSquare(sq.getColumn() - 2, sq.getRow()))) {
            return true;
        } else if (sq.getColumn() < 6
                && kingPossibleMoves.contains(board.getSquare(sq.getColumn() + 2, sq.getRow()))) {
            return true;
        }
        return false;
    }

    /**
     * Hashes given chessboard using previously generated square hashes and
     * bitwise XOR-operation. Chance of having two different board situations
     * with same hash is miniscule.
     *
     * @param board chessboard to be hashed.
     * @return hash of given chessboard.
     */
    public long hash(ChessBoard board) {
        long hash = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                hash ^= squareHashes[8 * i + j][numberOfPiece(board, board.getSquare(i, j))];
            }
        }

        return hash;
    }

}

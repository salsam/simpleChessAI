package chess.domain.board;

import chess.domain.GameSituation;
import chess.domain.datastructures.MyArrayList;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.addPieceToOwner;

/**
 * This class is used to make copies of ChessBoards with method copy as well as
 * make two chessboards deeply equal or undo a move by making its start point
 * and end point equal to backup.
 *
 * @author sami
 */
public class ChessBoardCopier {

    /**
     * Creates a new ChessBoardCopier-object.
     */
    public ChessBoardCopier() {
    }

    /**
     * Returns a new ChessBoard that deeply equals the one given as parameter.
     *
     * @param board chessboard to be copied.
     * @return a deep copy of given ChessBoard.
     */
    public static ChessBoard copy(ChessBoard board) {
        ChessBoard copy = new ChessBoard(board.getMovementLogic());
        copy.setTable(copyTable(board.getTable()));
        setPieces(copy);

        return copy;
    }

    private static Square[][] copyTable(Square[][] table) {
        Square[][] copyTable = new Square[table.length][table[0].length];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                copyTable[i][j] = table[i][j].clone();
            }
        }
        return copyTable;
    }

    private static void setPieces(ChessBoard board) {
        board.setBlackPieces(new MyArrayList());
        board.setWhitePieces(new MyArrayList());

        for (int i = 0; i < board.getTable().length; i++) {
            for (int j = 0; j < board.getTable()[0].length; j++) {
                addPieceToOwner(board.getSquare(i, j), board);
            }
        }
    }

    /**
     * This method is used to make first ChessBoard object given as parameter
     * deeply equal to second one without creating new objects. Thus this method
     * changes each field in first board to be equal to second one's.
     *
     * @param board ChessBoard to be changed to deeply equal second one
     * @param chessboard ChessBoard to be 'copied'
     */
    public static void revertOldSituation(ChessBoard board, ChessBoard chessboard) {
        clearBoardOfPieces(board);
        makePieceListsEqual(board, chessboard);
        putAllPiecesOnBoard(board);
    }

    private static void putAllPiecesOnBoard(ChessBoard board) {
        for (Player player : Player.values()) {
            board.getPieces(player).stream().forEach(piece -> {
                Square location = board.getTable()[piece.getColumn()][piece.getRow()];
                if (!piece.isTaken()) {
                    location.setPiece(piece);
                }
            });
        }
    }

    private static void makePieceListsEqual(ChessBoard board, ChessBoard chessboard) {
        for (Player player : Player.values()) {
            board.getPieces(player).stream().forEach(playersPiece -> {
                chessboard.getPieces(player).stream().forEach(piece -> {
                    if (piece.equals(playersPiece)) {
                        playersPiece.makeDeeplyEqualTo(piece);
                    }
                });
            });
        }
    }

    private static void clearBoardOfPieces(ChessBoard board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getTable()[i][j].setPiece(null);
            }

        }
    }

    /**
     * Offers a more effective way of reverting a single move instead of whole
     * series of moves.
     *
     * @param backUp backup of situation before move was made.
     * @param sit game situation after move was made.
     * @param from square that moved piece moved from.
     * @param to square that moved piece moved to.
     */
    public static void undoMove(ChessBoard backUp, GameSituation sit, Square from, Square to) {
        sit.decrementCountOfCurrentBoardSituation();
        sit.updateHashForUndoingMove(backUp, from, to);
        from.setPiece(to.getPiece());
        Piece old = backUp.getSquare(from.getColumn(), from.getRow()).getPiece();
        if (old.getClass() != from.getPiece().getClass()) {
            ChessBoardInitializer.removePieceFromOwner(from.getPiece(), sit.getChessBoard());
            ChessBoardInitializer.putPieceOnBoard(sit.getChessBoard(), old.clone());
        } else {
            from.getPiece().makeDeeplyEqualTo(old);
        }

        Piece taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();
        if (taken != null) {
            putTakenPieceBackOnBoard(sit.getChessBoard(), taken, to);
        } else {
            to.setPiece(null);
            handleCastling(from, to, sit, backUp);
            handleEnPassant(from, to, sit, backUp);
        }
    }

    private static void putTakenPieceBackOnBoard(
            ChessBoard board, Piece taken, Square to) {
        for (Piece piece : board.getPieces(taken.getOwner())) {
            if (piece.getPieceCode().equals(taken.getPieceCode())) {
                piece.setTaken(false);
                to.setPiece(piece);
                break;
            }
        }
    }

    private static void handleEnPassant(Square from, Square to, GameSituation sit, ChessBoard backUp) {
        if (from.getPiece().getClass() == Pawn.class
                && from.getColumn() != to.getColumn()) {
            to = sit.getChessBoard().getSquare(to.getColumn(), from.getRow());
            Piece taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();

            putTakenPieceBackOnBoard(sit.getChessBoard(), taken, to);
            sit.reHashBoard(false);
        }
    }

    private static void handleCastling(
            Square from, Square to, GameSituation sit, ChessBoard backUp) {
        if (from.getPiece().getClass() == King.class) {
            if (from.getColumn() - to.getColumn() == -2) {
                from = sit.getChessBoard().getSquare(7, from.getRow());
                to = sit.getChessBoard().getSquare(to.getColumn() - 1, to.getRow());
                sit.incrementCountOfCurrentBoardSituation();
                undoMove(backUp, sit, from, to);
            } else if (from.getColumn() - to.getColumn() == 2) {
                from = sit.getChessBoard().getSquare(0, from.getRow());
                to = sit.getChessBoard().getSquare(to.getColumn() + 1, to.getRow());
                sit.incrementCountOfCurrentBoardSituation();
                undoMove(backUp, sit, from, to);
            }
        }
    }
}

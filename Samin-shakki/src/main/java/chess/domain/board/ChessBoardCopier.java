package chess.domain.board;

import chess.domain.datastructures.MyArrayList;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
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
     * @param board chessboard that playing happens on.
     * @param backUp backUp of situation before move was made.
     * @param from square that moved piece moved from.
     * @param to square that moved piece moved to.
     */
    public static void undoMove(ChessBoard board, ChessBoard backUp, Square from, Square to) {
        from.setPiece(to.getPiece());
        from.getPiece().makeDeeplyEqualTo(
                backUp.getSquare(from.getColumn(), from.getRow()).getPiece());

        Piece taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();
        if (taken != null) {
            putTakenPieceBackOnBoard(board, taken, to);
        } else {
            to.setPiece(null);
            handleCastling(from, to, board, backUp);
            handleEnPassant(from, to, board, backUp);
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

    private static void handleEnPassant(Square from, Square to, ChessBoard board, ChessBoard backUp) {
        Piece taken;
        if (from.getPiece().getClass() == Pawn.class
                && from.getColumn() != to.getColumn()) {
            to = board.getSquare(to.getColumn(), from.getRow());
            taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();

            putTakenPieceBackOnBoard(board, taken, to);
        }
    }

    private static void handleCastling(
            Square from, Square to, ChessBoard board, ChessBoard backUp) {
        if (from.getPiece().getClass() == King.class
                && from.getColumn() - to.getColumn() == -2) {
            board.getSquare(to.getColumn() - 1, to.getRow()).getPiece()
                    .makeDeeplyEqualTo(backUp.getSquare(7, from.getRow()).getPiece());
            board.getSquare(7, from.getRow()).setPiece(
                    board.getSquare(to.getColumn() - 1, to.getRow()).getPiece());
            board.getSquare(to.getColumn() - 1, to.getRow()).setPiece(null);
        } else if (from.getPiece().getClass() == King.class
                && from.getColumn() - to.getColumn() == 2) {
            board.getSquare(to.getColumn() + 1, to.getRow()).getPiece()
                    .makeDeeplyEqualTo(backUp.getSquare(0, from.getRow()).getPiece());
            board.getSquare(0, from.getRow()).setPiece(
                    board.getSquare(to.getColumn() + 1, to.getRow()).getPiece());
            board.getSquare(to.getColumn() + 1, to.getRow()).setPiece(null);
        }
    }
}

package chess.domain.board;

import chess.domain.GameSituation;
import chess.domain.datastructures.MyArrayList;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
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
                addPieceToOwner(board.getTable()[i][j], board);
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
                location.setPiece(piece);
            });
        }
    }

    private static void makePieceListsEqual(ChessBoard board, ChessBoard chessboard) {
        for (Player player : Player.values()) {
            board.getPieces(player).stream().forEach(playersPiece -> {
                for (Piece piece : chessboard.getPieces(player)) {
                    if (piece.equals(playersPiece)) {
                        if (piece.getClass() == Pawn.class && playersPiece.getClass() == Queen.class) {
                            playersPiece = new Queen(piece.getColumn(), piece.getRow(), piece.getOwner(), piece.getPieceCode());
                            board.getSquare(piece.getColumn(), piece.getRow()).setPiece(playersPiece);
                        }
                        playersPiece.makeDeeplyEqualTo(piece);
                    }
                }
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
        if (from.getPiece() == null || old == null) {
            System.out.println("old: " + old + " cur: " + from.getPiece() + " from: " + from + " to " + to);
        }
        if (old.getClass() != from.getPiece().getClass()) {
            revertPromotion(from.getPiece(), sit.getChessBoard(), old);
        } else {
            from.getPiece().makeDeeplyEqualTo(old);
        }

        handleDestination(backUp, to, sit, from);
    }

    /**
     * Does exactly the same as above method but also updates piece to point to
     * new piece in square from. Thus piece will still point to same piece after
     * reverting promotion unlike above method.
     *
     * @param backUp backUp of situation before move was made.
     * @param sit current game situation.
     * @param from square that piece was situated on before movement.
     * @param moved piece that was moved.
     */
    public static void undoMove(ChessBoard backUp, GameSituation sit, Square from, Piece moved) {
        Square to = sit.getChessBoard().getSquare(moved.getColumn(), moved.getRow());
        sit.decrementCountOfCurrentBoardSituation();
        sit.updateHashForUndoingMove(backUp, from, to);

        if (!to.containsAPiece()) {
            System.out.println("Square that was moved to contains no pieces!");
        }

        from.setPiece(to.getPiece());
        Piece old = backUp.getSquare(from.getColumn(), from.getRow()).getPiece();
        if (from.getPiece() == null || old == null) {
            System.out.println("old: " + old + "cur: " + from.getPiece() + " from: " + from + " to " + to);
        }
        if (old.getClass() != from.getPiece().getClass()) {
            moved = revertPromotion(from.getPiece(), sit.getChessBoard(), old);
        }
        from.getPiece().makeDeeplyEqualTo(old);

        handleDestination(backUp, to, sit, from);
    }

    private static Piece revertPromotion(Piece current, ChessBoard board, Piece old) {
        if (old.getClass() != current.getClass()) {
            ChessBoardInitializer.removePieceFromOwner(current, board);
            ChessBoardInitializer.putPieceOnBoard(board, old.clone());
            current = board.getSquare(current.getColumn(), current.getRow()).getPiece();
        }
        return current;
    }

    private static void handleDestination(ChessBoard backUp, Square to, GameSituation sit, Square from) {
        Piece taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();
        if (taken != null) {
            putTakenPieceBackOnBoard(sit.getChessBoard(), taken, to);
        } else {
            to.setPiece(null);
            handleCastling(from, to, sit, backUp);
            handleEnPassant(from, to, sit, backUp);
        }
    }

    private static void putTakenPieceBackOnBoard(ChessBoard board, Piece taken, Square to) {
        for (Piece piece : board.getPieces(taken.getOwner())) {
            if (piece.getPieceCode().equals(taken.getPieceCode())) {
//                revertPromotion(piece, board, taken);
                piece.makeDeeplyEqualTo(taken);
                board.getSquare(taken.getColumn(), taken.getRow()).setPiece(piece);
                break;
            }
        }
    }

    private static void handleEnPassant(Square from, Square to, GameSituation sit, ChessBoard backUp) {
        if (from.getPiece().getClass() == Pawn.class && from.getColumn() != to.getColumn()) {
            to = sit.getChessBoard().getSquare(to.getColumn(), from.getRow());
            Piece taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();
            putTakenPieceBackOnBoard(sit.getChessBoard(), taken, to);
            sit.reHashBoard(false);
        }
    }

    private static void handleCastling(Square from, Square to, GameSituation sit, ChessBoard backUp) {
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

    /**
     * Checks if both chessboard have deeply equal pieces on same squares.
     *
     * @param cb1 first chessboard.
     * @param cb2 secod chessboard.
     * @return true if all squares contain deeply equal pieces.
     */
    public static boolean chessBoardsAreDeeplyEqual(ChessBoard cb1, ChessBoard cb2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!cb1.getSquare(i, j).getPiece().deepEquals(cb2.getSquare(i, j).getPiece())) {
                    return false;
                }
            }
        }

        return true;
    }
}

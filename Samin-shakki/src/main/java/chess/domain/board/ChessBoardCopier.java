package chess.domain.board;

import chess.domain.GameSituation;
import chess.domain.datastructures.MyArrayList;
import chess.domain.pieces.BetterPiece;
import static chess.domain.pieces.Klass.KING;
import static chess.domain.pieces.Klass.PAWN;
import static chess.domain.pieces.Klass.QUEEN;
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
    }

    private static void makePieceListsEqual(ChessBoard board, ChessBoard chessboard) {
        for (Player player : Player.values()) {
            board.getPieces(player).clear();
            for (BetterPiece playersPiece : chessboard.getPieces(player)) {
                ChessBoardInitializer.putPieceOnBoard(board, playersPiece.clone());
            }
        }
    }

    private static void clearBoardOfPieces(ChessBoard board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getTable()[i][j].setPiece(null);
            }

        }
        board.getPieces(Player.BLACK).clear();
        board.getPieces(Player.WHITE).clear();
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
        BetterPiece old = backUp.getSquare(from.getColumn(), from.getRow()).getPiece();
        sit.decrementCountOfCurrentBoardSituation();
        if (old.getKlass() == PAWN && to.getPiece().getKlass() == QUEEN) {
            sit.updateHashForUndoingPromotion(to);
        }
        sit.updateHashForUndoingMove(backUp, from, to);

        if (to.getPiece() == null || old == null) {
            System.out.println("old: " + old);
            System.out.println(" cur: " + to.getPiece());
            System.out.println(" from: " + from + " to " + to);
        }

        from.setPiece(to.getPiece());
        from.getPiece().makeDeeplyEqualTo(old);

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
//    public static Piece undoMove(ChessBoard backUp, GameSituation sit, Square from, Piece moved) {
//        Square to = sit.getChessBoard().getSquare(moved.getColumn(), moved.getRow());
//        sit.decrementCountOfCurrentBoardSituation();
//        sit.updateHashForUndoingMove(backUp, from, to);
//
//        if (!to.containsAPiece()) {
//            System.out.println("Square that piece was moved to contains no pieces!");
//        }
//
//        Piece old = backUp.getSquare(from.getColumn(), from.getRow()).getPiece();
//        if (moved == null || old == null || from.equals(new Square(6, 1))) {
//            System.out.println("old: " + old + "cur: " + moved + " owner " + moved.getOwner() + " from: " + from + " to " + to);
//            System.out.println("backUp: " + backUp.getSquare(from.getColumn(),
//                    from.getRow()).getPiece() + " at square: ("
//                    + from.getColumn() + "," + from.getRow() + ")");
//            System.out.println("backUp: " + backUp.getSquare(to.getColumn(),
//                    to.getRow()).getPiece() + " at square: ("
//                    + to.getColumn() + "," + to.getRow() + ")");
//            backUp.printTable();
//            System.out.println("");
//            sit.getChessBoard().printTable();
//        }
//
//        if (old.getClass() != moved.getClass()) {
//            revertPromotion(from, sit.getChessBoard(), old);
//            moved = sit.getChessBoard().getSquare(moved.getColumn(), moved.getRow()).getPiece();
//        }
//
//        from.setPiece(moved);
//        moved.setColumn(from.getColumn());
//        moved.setRow(from.getRow());
//
//        moved.makeDeeplyEqualTo(old);
//
//        handleDestination(backUp, to, sit, from);
//
//        return moved;
//    }
    private static void revertPromotion(Square from, ChessBoard board, BetterPiece old) {
        if (old.getClass() != from.getPiece().getClass()) {
            System.out.println(board.getSquare(from.getColumn(), from.getRow()).getPiece());
            System.out.println("VDBDFNTGMHYMU;");
            ChessBoardInitializer.removePieceFromOwner(from.getPiece(), board);
            ChessBoardInitializer.putPieceOnBoard(board, old.clone());
            System.out.println(board.getSquare(from.getColumn(), from.getRow()).getPiece());
        }
    }

    private static void handleDestination(ChessBoard backUp, Square to, GameSituation sit, Square from) {
        BetterPiece taken = backUp.getSquare(to.getColumn(), to.getRow()).getPiece();
        if (taken != null) {
            putTakenPieceBackOnBoard(sit.getChessBoard(), taken, to);
        } else {
            to.setPiece(null);
            handleCastling(from, to, sit, backUp);
            handleEnPassant(from, to, sit, backUp);
        }
    }

    private static void putTakenPieceBackOnBoard(ChessBoard board, BetterPiece taken, Square to) {
        for (BetterPiece piece : board.getPieces(taken.getOwner())) {
            if (piece.getPieceCode().equals(taken.getPieceCode())) {
                piece.makeDeeplyEqualTo(taken);
                board.getSquare(taken.getColumn(), taken.getRow()).setPiece(piece);
                break;
            }
        }
    }

    private static void handleEnPassant(Square from, Square to, GameSituation sit, ChessBoard backUp) {
        if (from.getPiece().getKlass() == PAWN && from.getColumn() != to.getColumn()) {
            Square to2 = sit.getChessBoard().getSquare(to.getColumn(), from.getRow());
            BetterPiece taken = backUp.getSquare(to2.getColumn(), to2.getRow()).getPiece();
            putTakenPieceBackOnBoard(sit.getChessBoard(), taken, to2);
            sit.reHashBoard(false);
        }
    }

    private static void handleCastling(Square from, Square to, GameSituation sit, ChessBoard backUp) {
        if (from.getPiece().getKlass() == KING) {
            if (from.getColumn() - to.getColumn() == -2) {
                Square from2 = sit.getChessBoard().getSquare(7, from.getRow());
                Square to2 = sit.getChessBoard().getSquare(to.getColumn() - 1, to.getRow());
                sit.incrementCountOfCurrentBoardSituation();
                undoMove(backUp, sit, from2, to2);
            } else if (from.getColumn() - to.getColumn() == 2) {
                Square from2 = sit.getChessBoard().getSquare(0, from.getRow());
                Square to2 = sit.getChessBoard().getSquare(to.getColumn() + 1, to.getRow());
                sit.incrementCountOfCurrentBoardSituation();
                undoMove(backUp, sit, from2, to2);
            }
        }
    }

    /**
     * Checks if both chessboard have deeply equal pieces on same squares.
     *
     * @param cb1 first chessboard.
     * @param cb2 second chessboard.
     * @return true if all squares contain deeply equal pieces.
     */
    public static boolean chessBoardsAreDeeplyEqual(ChessBoard cb1, ChessBoard cb2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cb1.getSquare(i, j).getPiece() == null) {
                    if (cb2.getSquare(i, j).getPiece() != null) {
                        System.out.println("cb1: " + cb1.getSquare(i, j).getPiece());
                        System.out.println("cb2: " + cb2.getSquare(i, j).getPiece());
                        cb1.printTable();
                        System.out.println("");
                        cb2.printTable();
                        return false;
                    }
                } else if (!cb1.getSquare(i, j).getPiece().deepEquals(cb2.getSquare(i, j).getPiece())) {
                    System.out.println("cb1: " + cb1.getSquare(i, j).getPiece());
                    System.out.println("cb2: " + cb2.getSquare(i, j).getPiece());
                    cb1.printTable();
                    System.out.println("");
                    cb2.printTable();
                    return false;
                }
            }
        }

        return true;
    }
}

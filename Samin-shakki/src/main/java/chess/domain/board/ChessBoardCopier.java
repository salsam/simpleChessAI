package chess.domain.board;

import static chess.logic.chessboardinitializers.ChessBoardInitializer.addPieceToOwner;
import java.util.ArrayList;

/**
 * This class is used to make copies of ChessBoards with method copy.
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
        board.setBlackPieces(new ArrayList());
        board.setWhitePieces(new ArrayList());

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
    public static void makeFirstChessBoardDeeplyEqualToSecond(ChessBoard board, ChessBoard chessboard) {
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
}

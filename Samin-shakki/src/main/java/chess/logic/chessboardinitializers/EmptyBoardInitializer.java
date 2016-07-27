package chess.logic.chessboardinitializers;

import chess.domain.board.ChessBoard;

/**
 * This class is responsible for cleaning a ChessBoard of all pieces.
 *
 * @author samisalo
 */
public class EmptyBoardInitializer extends ChessBoardInitializer {

    /**
     * Initialises the ChessBoard given as parameter to be empty. Thus board
     * will be cleaned of all pieces.
     *
     * @param board board to be initialised
     */
    @Override
    public void initialize(ChessBoard board) {
        clearBoard(board);
    }

}

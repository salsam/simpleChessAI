package chess.logic.gamelogic;

import chess.domain.board.ChessBoard;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;

/**
 *
 * @author sami
 */
public class PromotionLogic {

    public static void promotePiece(Piece piece, ChessBoard cb) {
        if (piece.getClass() == Pawn.class && !piece.isTaken()) {
            Pawn chosenPawn = (Pawn) piece;
            if (chosenPawn.opposingEnd() == piece.getRow()) {
                Queen promoted = new Queen(chosenPawn.getColumn(), chosenPawn.getRow(),
                        chosenPawn.getOwner(), chosenPawn.getPieceCode());
                ChessBoardInitializer.removePieceFromOwner(chosenPawn, cb);
                putPieceOnBoard(cb, promoted);
                piece = promoted;
            }
        }
    }
}

package chess.logic.gamelogic;

import chess.domain.GameSituation;
import chess.domain.pieces.BetterPiece;
import static chess.domain.pieces.Klass.PAWN;
import static chess.domain.pieces.Klass.QUEEN;

/**
 *
 * @author sami
 */
public class PromotionLogic {

    public static void promotePiece(BetterPiece piece, GameSituation sit) {
        if (piece.getKlass() == PAWN && !piece.isTaken()) {
            if (piece.isAtOpposingEnd()) {
                sit.updateHashForPromotion(
                        sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow()));
                piece.setKlass(QUEEN);
//                System.out.println("Pawn was promoted111111111111");
            }
        }
    }
}

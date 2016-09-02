package chess.logic.gamelogic;

import chess.domain.board.ChessBoard;
import chess.domain.pieces.BetterPiece;
import static chess.domain.pieces.Klass.PAWN;
import static chess.domain.pieces.Klass.QUEEN;
import chess.domain.pieces.Piece;

/**
 *
 * @author sami
 */
public class PromotionLogic {

    public static void promotePiece(Piece piece, ChessBoard cb) {
        if (((BetterPiece) piece).getKlass() == PAWN && !piece.isTaken()) {
            //Hashin pävitys tähän!!!!!!!
            if (((BetterPiece) piece).isAtOpposingEnd()) {
                ((BetterPiece) piece).setKlass(QUEEN);
                System.out.println("Pawn was promoted111111111111");
            }
        }
    }
}

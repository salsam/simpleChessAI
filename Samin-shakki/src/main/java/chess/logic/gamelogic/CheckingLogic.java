package chess.logic.gamelogic;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import chess.domain.board.ChessBoardCopier;
import chess.domain.board.Square;
import chess.domain.pieces.King;
import chess.domain.pieces.Piece;

/**
 * This class is responsible for checking if king is checked, checkmated or
 * stalemated in the game given to it as parameter. After checking that it will
 * act according to situation to continue game flow.
 *
 * @author sami
 */
public class CheckingLogic {

    /**
     * GameSituation that this class will check for check, checkmate and
     * stalemate.
     */
    private GameSituation game;

    /**
     * Creates a new CheckingLogic-object for given game.
     *
     * @param game game that this checkingLogic will be applied for.
     */
    public CheckingLogic(GameSituation game) {
        this.game = game;
    }

    /**
     * Updates set containing all squares that opponent threatens and then
     * checks if player's king is on one of those.
     *
     * @param player player being checked
     * @return true if player's king is threatened by opposing piece
     */
    public boolean checkIfChecked(Player player) {
        King playersKing = game.getChessBoard().getKings().get(player);
        game.getChessBoard().updateThreatenedSquares(getOpponent(player));
        return game.getChessBoard().threatenedSquares(getOpponent(player)).contains(game.getChessBoard().getSquare(playersKing.getColumn(), playersKing.getRow()));
    }

    /**
     * Checks whether or not player is checkmated in this game.
     *
     * @param player player who is possibly checkmated.
     * @return true if player is checkmated. Else false.
     */
    public boolean checkMate(Player player) {
        ChessBoard backUp = ChessBoardCopier.copy(game.getChessBoard());
        for (Piece piece : game.getChessBoard().getPieces(player)) {
            if (piece.isTaken()) {
                continue;
            }
            game.getChessBoard().updateThreatenedSquares(getOpponent(player));
            for (Square possibility : game.getChessBoard().getMovementLogic().possibleMoves(piece, game.getChessBoard())) {
                game.getChessBoard().getMovementLogic().move(piece, possibility, game.getChessBoard());
                game.getChessBoard().updateThreatenedSquares(getOpponent(player));
                if (!checkIfChecked(player)) {
                    ChessBoardCopier.makeFirstChessBoardDeeplyEqualToSecond(game.getChessBoard(), backUp);
                    return false;
                }
                ChessBoardCopier.makeFirstChessBoardDeeplyEqualToSecond(game.getChessBoard(), backUp);;
            }
        }
        ChessBoardCopier.makeFirstChessBoardDeeplyEqualToSecond(game.getChessBoard(), backUp);;
        game.setContinues(false);

        return true;
    }

    /**
     * Returns whether or not chosen player has any legal moves.
     *
     * @param player chosen player
     * @return true player has no legal moves otherwise false.
     */
    public boolean stalemate(Player player) {
        for (Piece piece : game.getChessBoard().getPieces(player)) {
            if (!game.getChessBoard().getMovementLogic().possibleMoves(piece, game.getChessBoard()).isEmpty()) {
                return false;
            }
        }
        game.setContinues(false);
        return true;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template column, choose Tools | Templates
 * and open the template in the editor.
 */
package chess.logic.movementlogic.piecemovers;

import chess.domain.board.ChessBoard;
import java.util.Set;
import chess.domain.board.Square;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Rook;
import java.util.HashSet;

/**
 * This class is responsible for all rook-related movement logic.
 *
 * @author sami
 */
public class RookMover extends PieceMover {

    /**
     * Creates a new RookMover-object.
     */
    public RookMover() {
    }

    /**
     * This method moves rook on the board and saves true to field hasBeenMoved.
     *
     * @param target square this rook is moving to.
     * @param board board on which movement happens.
     */
    @Override
    public void move(Piece piece, Square target, ChessBoard board) {

        if (piece == null || piece.getClass() != Rook.class) {
            return;
        }

        Rook rook = (Rook) piece;
        rook.setHasBeenMoved(true);
        super.move(rook, target, board);
    }

    /**
     * Return a list containing all squares that this rook threatens.
     *
     * @param board board on which this rook moves
     * @return list containing all squares this rook threatens
     */
    @Override
    public Set<Square> threatenedSquares(Piece piece, ChessBoard board) {
        Rook rook = (Rook) piece;
        Set<Square> possibilities = new HashSet<>();
        addHorizontalPossibilities(board.getSquare(rook.getColumn(), rook.getRow()), board, possibilities);
        addVerticalPossibilities(board.getSquare(rook.getColumn(), rook.getRow()), board, possibilities);

        return possibilities;
    }
}

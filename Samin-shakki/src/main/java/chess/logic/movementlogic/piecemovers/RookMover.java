/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template column, choose Tools | Templates
 * and open the template in the editor.
 */
package chess.logic.movementlogic.piecemovers;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import java.util.Set;
import chess.domain.board.Square;
import chess.domain.datastructures.MyHashSet;
import chess.domain.pieces.BetterPiece;
import chess.domain.pieces.Piece;

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
     */
    @Override
    public void move(Piece piece, Square target, GameSituation sit) {

        BetterPiece rook = (BetterPiece) piece;
        rook.setHasBeenMoved(true);
        super.move(rook, target, sit);
    }

    /**
     * Return a list containing all squares that this rook threatens.
     *
     * @param board board on which this rook moves
     * @return list containing all squares this rook threatens
     */
    @Override
    public Set<Square> threatenedSquares(Piece piece, ChessBoard board) {
        BetterPiece rook = (BetterPiece) piece;
        Set<Square> possibilities = new MyHashSet<>();
        addHorizontalPossibilities(board.getSquare(rook.getColumn(), rook.getRow()), board, possibilities);
        addVerticalPossibilities(board.getSquare(rook.getColumn(), rook.getRow()), board, possibilities);

        return possibilities;
    }
}

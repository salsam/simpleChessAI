/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template column, choose Tools | Templates
 * and open the template in the editor.
 */
package chess.logic.movementlogic.piecemovers;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import java.util.Set;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import chess.domain.board.Square;
import chess.domain.datastructures.MyHashSet;
import chess.domain.pieces.BetterPiece;
import static chess.domain.pieces.Klass.ROOK;
import chess.domain.pieces.Piece;

/**
 * This class is responsible for all king-related movement logic.
 *
 * @author sami
 */
public class KingMover extends PieceMover {

    /**
     * Creates a new KingMover-object.
     */
    public KingMover() {
    }

    /**
     * This method moves king on the board and saves true to field hasBeenMoved.
     * If movement is castling, this method also moves the chosen rook to
     * correct square. Castling is noticed from king moving two squares.
     *
     * @param piece target king for movement
     * @param target square this king is moving to.
     * @param sit situation being changed.
     */
    @Override
    public void move(Piece piece, Square target, GameSituation sit) {

//        if (piece == null || piece.getClass() != King.class) {
//            return;
//        }
        BetterPiece king = (BetterPiece) piece;
        king.setHasBeenMoved(true);
        RookMover rookMover = new RookMover();

        castleIfChosen(king, target, sit, rookMover);

        super.move(king, target, sit);
    }

    private void castleIfChosen(BetterPiece king, Square target, GameSituation sit, RookMover rookMover) {
        if (king.getColumn() - target.getColumn() == 2) {
            sit.decrementCountOfCurrentBoardSituation();
            BetterPiece rook = (BetterPiece) sit.getChessBoard().getSquare(0, king.getRow()).getPiece();
            rookMover.move(rook,
                    sit.getChessBoard().getSquare(target.getColumn() + 1, target.getRow()), sit);
            sit.reHashBoard(true);
        } else if (king.getColumn() - target.getColumn() == -2) {
            sit.decrementCountOfCurrentBoardSituation();
            BetterPiece rook = (BetterPiece) sit.getChessBoard().getSquare(7, king.getRow()).getPiece();
            rookMover.move(rook,
                    sit.getChessBoard().getSquare(target.getColumn() - 1, target.getRow()), sit);
            sit.reHashBoard(true);
        }
    }

    /**
     * Return a list containing all squares that target king threatens.
     *
     * @param piece target king
     * @param board board where this king moves
     * @return list containing all squares target king threatens
     */
    @Override
    public Set<Square> threatenedSquares(Piece piece, ChessBoard board) {
        BetterPiece king = (BetterPiece) piece;
        int[] columnChange = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};
        int[] rowChange = new int[]{1, 1, 1, 0, 0, -1, -1, -1};

        return possibilities(board.getSquare(king.getColumn(), king.getRow()), columnChange, rowChange, board);
    }

    /**
     * Returns a list containing all squares chosen king can legally move to.
     * That means all neighbour squares of king's location that aren't
     * threatened by opponent or contain player's own piece.
     *
     * @param piece target king
     * @param board chessboard on which movement happens
     * @return a list containing all squares target king can legally move to.
     */
    @Override
    public Set<Square> possibleMoves(Piece piece, ChessBoard board) {
        Set<Square> moves = new MyHashSet<>();
        BetterPiece king = (BetterPiece) piece;
        board.updateThreatenedSquares(getOpponent(piece.getOwner()));

        threatenedSquares(king, board).stream()
                .filter((target) -> (legalToMoveTo(king, target, board) && !isThreatenedByOpponent(king.getOwner(), target, board)))
                .forEach((target) -> {
                    moves.add(target);
                });
        addCastling(king, board, moves);

        return moves;
    }

    private boolean isThreatenedByOpponent(Player player, Square target, ChessBoard board) {
        return board.threatenedSquares(getOpponent(player)).contains(target);
    }

    private void addCastling(BetterPiece king, ChessBoard board, Set<Square> possibilities) {
        int[] cols = new int[]{0, 7};
        if (!king.isHasBeenMoved()) {
            for (int i = 0; i < 2; i++) {
                if (board.getSquare(cols[i], king.getRow()).containsAPiece()) {
                    Piece piece = board.getSquare(cols[i], king.getRow()).getPiece();
                    if (((BetterPiece) piece).getKlass() == ROOK && piece.getOwner() == king.getOwner()) {
                        BetterPiece rook = (BetterPiece) piece;
                        addCastlingIfPossible(king, rook, board, possibilities);
                    }
                }
            }
        }
    }

    private void addCastlingIfPossible(BetterPiece king, BetterPiece rook, ChessBoard board, Set<Square> possibilities) {
        if (!rook.isHasBeenMoved()) {
            if (rook.getColumn() < king.getColumn()) {
                addPossibilityToCastleRight(king, rook, board, possibilities);
            } else {
                addPossibilityToCastleLeft(king, rook, board, possibilities);
            }
        }
    }

    private void addPossibilityToCastleLeft(BetterPiece king, BetterPiece rook, ChessBoard board, Set<Square> possibilities) {
        if (squaresAreAllEmpty(board, king.getColumn(), rook.getColumn(), king.getRow())) {
            if (squaresAreAllUnthreatened(board, getOpponent(king.getOwner()), king.getColumn(), king.getColumn() + 2, king.getRow())) {
                possibilities.add(board.getSquare(king.getColumn() + 2, king.getRow()));
            }
        }
    }

    private void addPossibilityToCastleRight(BetterPiece king, BetterPiece rook, ChessBoard board, Set<Square> possibilities) {
        if (squaresAreAllEmpty(board, rook.getColumn(), king.getColumn(), king.getRow())) {
            if (squaresAreAllUnthreatened(board, getOpponent(king.getOwner()), king.getColumn() - 2, king.getColumn(), king.getRow())) {
                possibilities.add(board.getSquare(king.getColumn() - 2, king.getRow()));
            }
        }
    }

    private boolean squaresAreAllUnthreatened(ChessBoard board, Player opponent, int minCol, int maxCol, int row) {
        Set<Square> threatenedSquares = board.threatenedSquares(opponent);
        for (int col = minCol; col < maxCol + 1; col++) {
            if (threatenedSquares.contains(board.getSquare(col, row))) {
                return false;
            }
        }
        return true;
    }

    private boolean squaresAreAllEmpty(ChessBoard board, int minCol, int maxCol, int row) {
        for (int col = minCol + 1; col < maxCol; col++) {
            if (board.getSquare(col, row).containsAPiece()) {
                return false;
            }
        }
        return true;
    }
}

package com.lacavedeharol.chess.model.validation;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;

public class QueenMoveValidation implements MoveValidationStrategy {

    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        boolean straight = (fromFile == toFile || fromRank == toRank);
        boolean diagonal = (Math.abs(toFile - fromFile) == Math.abs(toRank - fromRank));

        if (!straight && !diagonal) {
            return false;
        }

        if (straight && !MoveUtils.isPathClearStraight(fromFile, fromRank, toFile, toRank, gameState)) {
            return false;
        }
        if (diagonal && !MoveUtils.isPathClearDiagonal(fromFile, fromRank, toFile, toRank, gameState)) {
            return false;
        }
        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }
}

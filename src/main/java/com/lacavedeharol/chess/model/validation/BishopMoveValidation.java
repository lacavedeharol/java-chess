package com.lacavedeharol.chess.model.validation;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;

public class BishopMoveValidation implements MoveValidationStrategy {

    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        if (Math.abs(toFile - fromFile) != Math.abs(toRank - fromRank)) {
            return false;
        }
        if (!MoveUtils.isPathClearDiagonal(fromFile, fromRank, toFile, toRank, gameState)) {
            return false;
        }
        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }
}

package com.lacavedeharol.chess.model.validation;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;

public class KnightMoveValidation implements MoveValidationStrategy {

    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        int fileDiff = Math.abs(toFile - fromFile);
        int rankDiff = Math.abs(toRank - fromRank);

        boolean pattern = (fileDiff == 2 && rankDiff == 1) || (fileDiff == 1 && rankDiff == 2);
        if (!pattern) {
            return false;
        }

        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }
}

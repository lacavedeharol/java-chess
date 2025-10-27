package com.lacavedeharol.chess.model.validation;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;
import com.lacavedeharol.chess.model.PieceType;

public class KingMoveValidation implements MoveValidationStrategy {

    @Override
    public boolean isValidMove(ChessPiece king, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        int fileDiff = Math.abs(toFile - fromFile);
        int rankDiff = Math.abs(toRank - fromRank);

        // Normal king move: 1 square in any direction.
        if (fileDiff <= 1 && rankDiff <= 1 && (fileDiff + rankDiff > 0)) {
            ChessPiece target = gameState.getPieceAt(toFile, toRank);
            return target == null || target.isWhite() != king.isWhite();
        }

        // Castling Move:
        /*
         * Neither king nor Rook should have been moved.
         * King cannot be in check.
         */
        else if (rankDiff == 0 && fileDiff == 2) {

            if (king.hasMoved()) {
                return false;
            }

            if (gameState.isSquareUnderAttack(fromFile, fromRank, !king.isWhite())) {
                return false;
            }

            // Determine if it's King-side or Queen-side.
            if (toFile > fromFile) { // King-side castling
                // Rook must be at the corner and must not have moved.
                ChessPiece rook = gameState.getPieceAt(7, fromRank);
                if (rook == null || rook.getPieceType() != PieceType.ROOK || rook.hasMoved()) {
                    return false;
                }

                // Path between King and Rook must be clear.
                if (gameState.getPieceAt(5, fromRank) != null || gameState.getPieceAt(6, fromRank) != null) {
                    return false;
                }

                // Squares the king passes through cannot be under attack.
                if (gameState.isSquareUnderAttack(5, fromRank, !king.isWhite()) ||
                        gameState.isSquareUnderAttack(6, fromRank, !king.isWhite())) {
                    return false;
                }

                return true;
            } else { // Queen-side castling.
                // Rook must be at the corner and must not have moved.
                ChessPiece rook = gameState.getPieceAt(0, fromRank);
                if (rook == null || rook.getPieceType() != PieceType.ROOK || rook.hasMoved()) {
                    return false;
                }

                // Path between King and Rook must be clear.
                if (gameState.getPieceAt(1, fromRank) != null ||
                        gameState.getPieceAt(2, fromRank) != null ||
                        gameState.getPieceAt(3, fromRank) != null) {
                    return false;
                }

                // Squares the king passes through cannot be under attack.
                if (gameState.isSquareUnderAttack(2, fromRank, !king.isWhite()) ||
                        gameState.isSquareUnderAttack(3, fromRank, !king.isWhite())) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }
}

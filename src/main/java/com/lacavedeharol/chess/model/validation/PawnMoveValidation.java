package com.lacavedeharol.chess.model.validation;

import java.awt.Point;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;

public class PawnMoveValidation implements MoveValidationStrategy {

    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {

        boolean isWhite = piece.isWhite();
        int direction = isWhite ? -1 : 1; // For white, rank decreases; for black, it increases.

        // Standard forward move.
        if (toFile == fromFile) {
            ChessPiece target = gameState.getPieceAt(toFile, toRank);
            // Single step forward.
            if (toRank == fromRank + direction) {
                return target == null;
            }
            // Double step forward.
            if (toRank == fromRank + 2 * direction) {
                if (piece.hasMoved() || target != null) {
                    return false;
                }
                // Check intermediate square.
                return gameState.getPieceAt(fromFile, fromRank + direction) == null;
            }
        }
        // Diagonal Moves (Standard Capture OR En Passant).
        if (Math.abs(toFile - fromFile) == 1 && toRank == fromRank + direction) {
            // Standard diagonal capture.
            ChessPiece target = gameState.getPieceAt(toFile, toRank);
            if (target != null && target.isWhite() != piece.isWhite()) {
                return true;
            }
            // En Passant capture.
            // The move is also valid if the destination is the en passant target square.
            Point enPassantTarget = gameState.getEnPassantTargetSquare();
            if (enPassantTarget != null && toFile == enPassantTarget.x && toRank == enPassantTarget.y) {
                return true;
            }
        }

        // All other moves are invalid for a pawn.
        return false;
    }
}

package com.lacavedeharol.chess.model;

/**
 * Represents the outcome of a move attempt.
 */
public enum MoveResult {
    SUCCESS, // The move was successful.
    INVALID, // The move was illegal.
    PROMOTION_REQUIRED // The move was successful, but a pawn promotion is required.
}

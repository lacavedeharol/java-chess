package com.lacavedeharol.chess.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Upgraded AI with hard difficulty (depth 3 minimax + positional
 * evaluation).
 */
public class ImprovedAI {

    private final boolean isWhite;
    private final Random random = new Random();
    private static final int SEARCH_DEPTH = 3; // Look ahead 3 moves

    // Piece-square tables for positional evaluation
    private static final int[][] PAWN_TABLE = {
            { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 50, 50, 50, 50, 50, 50, 50, 50 },
            { 10, 10, 20, 30, 30, 20, 10, 10 },
            { 5, 5, 10, 25, 25, 10, 5, 5 },
            { 0, 0, 0, 20, 20, 0, 0, 0 },
            { 5, -5, -10, 0, 0, -10, -5, 5 },
            { 5, 10, 10, -20, -20, 10, 10, 5 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    private static final int[][] KNIGHT_TABLE = {
            { -50, -40, -30, -30, -30, -30, -40, -50 },
            { -40, -20, 0, 0, 0, 0, -20, -40 },
            { -30, 0, 10, 15, 15, 10, 0, -30 },
            { -30, 5, 15, 20, 20, 15, 5, -30 },
            { -30, 0, 15, 20, 20, 15, 0, -30 },
            { -30, 5, 10, 15, 15, 10, 5, -30 },
            { -40, -20, 0, 5, 5, 0, -20, -40 },
            { -50, -40, -30, -30, -30, -30, -40, -50 }
    };

    private static final int[][] KING_TABLE_MIDGAME = {
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -20, -30, -30, -40, -40, -30, -30, -20 },
            { -10, -20, -20, -20, -20, -20, -20, -10 },
            { 20, 20, 0, 0, 0, 0, 20, 20 },
            { 20, 30, 10, 0, 0, 10, 30, 20 }
    };

    public ImprovedAI(boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * Makes the best move using minimax algorithm with alpha-beta pruning.
     * 
     * @param gameState
     */
    public void makeMove(GameState gameState) {
        if (gameState.isWhiteToMove() != this.isWhite) {
            return;
        }

        List<Move> allPossibleMoves = getAllLegalMoves(gameState);

        if (allPossibleMoves.isEmpty()) {
            System.out.println("AI has no moves. Game over?");
            return;
        }

        Move bestMove = findBestMove(gameState, allPossibleMoves);

        if (bestMove != null) {
            MoveResult result = gameState.movePiece(
                    bestMove.fromFile, bestMove.fromRank,
                    bestMove.toFile, bestMove.toRank);

            // Always promote to queen
            if (result == MoveResult.PROMOTION_REQUIRED) {
                gameState.promotePawn(bestMove.toFile, bestMove.toRank, PieceType.QUEEN);
            }
        } else {
            // Fallback to random (shouldn't happen)
            Move randomMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            gameState.movePiece(randomMove.fromFile, randomMove.fromRank,
                    randomMove.toFile, randomMove.toRank);
        }
    }

    /**
     * Finds the best move using minimax with alpha-beta pruning.
     * 
     * @param gameState
     * @param moves
     * @return
     */
    private Move findBestMove(GameState gameState, List<Move> moves) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            ChessPiece movingPiece = gameState.getPieceAt(move.fromFile, move.fromRank);
            ChessPiece capturedPiece = gameState.makeHypotheticalMove(
                    move.fromFile, move.fromRank, move.toFile, move.toRank);

            // Use minimax to evaluate this move
            int score = -minimax(gameState, SEARCH_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            gameState.undoHypotheticalMove(
                    move.fromFile, move.fromRank, move.toFile, move.toRank,
                    movingPiece, capturedPiece);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Minimax algorithm with alpha-beta pruning.
     * Recursively evaluates positions to find the best move.
     * 
     * @param gameState
     * @param depth
     * @param alpha
     * @param beta
     * @param isMaximizing
     * @return
     */
    private int minimax(GameState gameState, int depth, int alpha, int beta, boolean isMaximizing) {
        // Base case: reached maximum depth or game over
        if (depth == 0) {
            return evaluateBoard(gameState);
        }

        List<Move> moves = getAllLegalMoves(gameState);

        if (moves.isEmpty()) {
            // Game over - checkmate or stalemate
            if (gameState.isWhiteKingInCheck() || gameState.isBlackKingInCheck()) {
                // Checkmate - heavily penalize
                return isMaximizing ? Integer.MIN_VALUE + 1000 : Integer.MAX_VALUE - 1000;
            }
            return 0; // Stalemate
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                ChessPiece piece = gameState.getPieceAt(move.fromFile, move.fromRank);
                ChessPiece captured = gameState.makeHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank);

                int eval = minimax(gameState, depth - 1, alpha, beta, false);

                gameState.undoHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank, piece, captured);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) {
                    break; // Beta cutoff - prune this branch
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                ChessPiece piece = gameState.getPieceAt(move.fromFile, move.fromRank);
                ChessPiece captured = gameState.makeHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank);

                int eval = minimax(gameState, depth - 1, alpha, beta, true);

                gameState.undoHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank, piece, captured);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) {
                    break; // Alpha cutoff - prune this branch
                }
            }
            return minEval;
        }
    }

    /**
     * Evaluates the current board position.
     * Higher score = better for AI, lower = better for opponent.
     * 
     * @param gameState
     * @return
     */
    private int evaluateBoard(GameState gameState) {
        int score = 0;

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    int positionalValue = getPositionalValue(piece, file, rank);

                    int totalValue = pieceValue + positionalValue;

                    // Positive for AI's pieces, negative for opponent's
                    if (piece.isWhite() == this.isWhite) {
                        score += totalValue;
                    } else {
                        score -= totalValue;
                    }
                }
            }
        }

        // Additional strategic factors
        score += evaluateCenterControl(gameState);
        score += evaluateKingSafety(gameState);

        return score;
    }

    /**
     * Gets the material value of a piece.
     * 
     * @param piece
     * @return
     */
    private int getPieceValue(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> 100;
            case KNIGHT -> 320;
            case BISHOP -> 330;
            case ROOK -> 500;
            case QUEEN -> 900;
            case KING -> 20000;
        };
    }

    /**
     * Gets positional bonus based on piece-square tables.
     * 
     * @param piece
     * @param file
     * @param rank
     * @return
     */
    private int getPositionalValue(ChessPiece piece, int file, int rank) {
        // Flip rank for black pieces (they play from opposite side)
        int tableRank = piece.isWhite() ? (7 - rank) : rank;

        return switch (piece.getPieceType()) {
            case PAWN -> PAWN_TABLE[tableRank][file];
            case KNIGHT -> KNIGHT_TABLE[tableRank][file];
            case KING -> KING_TABLE_MIDGAME[tableRank][file];
            default -> 0;
        };
    }

    /**
     * Bonus for controlling the center of the board.
     * 
     * @param gameState
     * @return
     */
    private int evaluateCenterControl(GameState gameState) {
        int score = 0;
        int[] centerFiles = { 3, 4 };
        int[] centerRanks = { 3, 4 };

        for (int file : centerFiles) {
            for (int rank : centerRanks) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null) {
                    int bonus = 10;
                    if (piece.isWhite() == this.isWhite) {
                        score += bonus;
                    } else {
                        score -= bonus;
                    }
                }
            }
        }

        return score;
    }

    /**
     * Penalty for being in check.
     * 
     * @param gameState
     * @return
     */
    private int evaluateKingSafety(GameState gameState) {
        int score = 0;

        if (this.isWhite && gameState.isWhiteKingInCheck()) {
            score -= 50;
        } else if (!this.isWhite && gameState.isBlackKingInCheck()) {
            score -= 50;
        }

        return score;
    }

    /**
     * Gets all legal moves for the AI's pieces.
     * 
     * @param gameState
     * @return
     */
    private List<Move> getAllLegalMoves(GameState gameState) {
        List<Move> moves = new ArrayList<>();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null && piece.isWhite() == this.isWhite) {
                    List<Point> legalMoves = gameState.getLegalMovesForPiece(file, rank);
                    for (Point to : legalMoves) {
                        moves.add(new Move(file, rank, to.x, to.y));
                    }
                }
            }
        }

        return moves;
    }

    public boolean isWhite() {
        return this.isWhite;
    }

    /**
     * Inner class to represent a move.
     */
    private static class Move {
        final int fromFile, fromRank, toFile, toRank;

        Move(int fromFile, int fromRank, int toFile, int toRank) {
            this.fromFile = fromFile;
            this.fromRank = fromRank;
            this.toFile = toFile;
            this.toRank = toRank;
        }
    }
}
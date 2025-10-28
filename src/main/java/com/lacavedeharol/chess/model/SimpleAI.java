package com.lacavedeharol.chess.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleAI {

    private final boolean isWhite; // Is this AI playing as White or Black?
    private final Random random = new Random();

    public SimpleAI(boolean isWhite) {
        this.isWhite = isWhite;
    }

    private int evaluateBoard(GameState gameState) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                ChessPiece piece = gameState.getPieceAt(f, r);
                if (piece != null) {
                    score += getPieceValue(piece);
                }
            }
        }
        return score;
    }

    private int getPieceValue(ChessPiece piece) {
        int value;
        switch (piece.getPieceType()) {
            case PAWN -> value = 10;
            case KNIGHT -> value = 30;
            case BISHOP -> value = 30;
            case ROOK -> value = 50;
            case QUEEN -> value = 90;
            case KING -> value = 900;
            default -> value = 0;

        }
        // Return positive for our pieces, negative for the opponent's.
        return piece.isWhite() == this.isWhite ? value : -value;
    }

    public boolean isWhite() {
        return this.isWhite;
    }

    public void makeMove(GameState gameState) {
        // Only make a move if it's our turn.
        if (gameState.isWhiteToMove() != this.isWhite) {
            return;
        }

        // Get all possible moves.
        List<Point[]> allPossibleMoves = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null && piece.isWhite() == this.isWhite) {
                    List<Point> legalMoves = gameState.getLegalMovesForPiece(file, rank);
                    for (Point move : legalMoves) {
                        allPossibleMoves.add(new Point[] { new Point(file, rank), move });
                    }
                }
            }
        }

        if (allPossibleMoves.isEmpty()) {
            System.out.println("AI has no moves. Game over?");
            return;
        }

        // Find the BEST move.
        Point[] bestMove = null;
        int bestScore = Integer.MIN_VALUE; // Start with the worst possible score.

        for (Point[] move : allPossibleMoves) {
            Point from = move[0];
            Point to = move[1];
            ChessPiece movingPiece = gameState.getPieceAt(from.x, from.y);

            // Simulate the move.
            ChessPiece capturedPiece = gameState.makeHypotheticalMove(from.x, from.y, to.x, to.y);

            // Evaluate the board after the move.
            int currentScore = evaluateBoard(gameState);

            // Undo the move
            gameState.undoHypotheticalMove(from.x, from.y, to.x, to.y, movingPiece, capturedPiece);

            // If this move is better than the best one we've found so far, save it.
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
            }
        }

        // Make the best move possible.
        if (bestMove != null) {
            Point from = bestMove[0];
            Point to = bestMove[1];
            System.out.println("AI chose best move from (" + from.x + "," + from.y + ") to (" + to.x + "," + to.y
                    + ") with score: " + bestScore);
            gameState.movePiece(from.x, from.y, to.x, to.y);
        } else {
            // Fallback to a random move if something went wrong (shouldn't happen).
            Point[] randomMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            gameState.movePiece(randomMove[0].x, randomMove[0].y, randomMove[1].x, randomMove[1].y);
        }
    }
}
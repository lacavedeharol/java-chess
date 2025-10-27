package com.lacavedeharol.chess.model.validation;

import com.lacavedeharol.chess.model.GameState;

public final class MoveUtils {

    private MoveUtils() {
    }

    /**
     * Checks if the path is clear for a straight-line move (horizontal or
     * vertical).
     *
     * @param fromFile
     * @param fromRank
     * @param toFile
     * @param toRank
     * @param gameState
     * @return true for a clear path, false for a blocked path.
     */
    public static boolean isPathClearStraight(int fromFile, int fromRank,
            int toFile, int toRank,
            GameState gameState) {

        if (fromFile == toFile) { // Vertical move.
            int step = Integer.signum(toRank - fromRank);
            // Loop from the square after the start to the one before the end.
            for (int r = fromRank + step; r != toRank; r += step) {
                if (gameState.getPieceAt(fromFile, r) != null) {
                    return false;
                }
            }
        } else { // Horizontal move (guaranteed by validator).
            int step = Integer.signum(toFile - fromFile);
            // Loop from the square after the start to the one before the end.
            for (int f = fromFile + step; f != toFile; f += step) {
                if (gameState.getPieceAt(f, fromRank) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the path is clear for a diagonal move.
     *
     * @param fromFile
     * @param fromRank
     * @param toFile
     * @param toRank
     * @param gameState
     * @return true for a clear path, false for a blocked path.
     */
    public static boolean isPathClearDiagonal(int fromFile, int fromRank,
            int toFile, int toRank,
            GameState gameState) {

        int fileStep = Integer.signum(toFile - fromFile);
        int rankStep = Integer.signum(toRank - fromRank);

        int currentFile = fromFile + fileStep;
        int currentRank = fromRank + rankStep;

        // Loop until we've reached the destination square.
        while (currentFile != toFile) {
            if (gameState.getPieceAt(currentFile, currentRank) != null) {
                return false;
            }
            currentFile += fileStep;
            currentRank += rankStep;
        }
        return true;
    }
}

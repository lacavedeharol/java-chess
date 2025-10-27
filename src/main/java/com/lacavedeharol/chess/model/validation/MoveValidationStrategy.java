package com.lacavedeharol.chess.model.validation;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;

public interface MoveValidationStrategy {

    boolean isValidMove(ChessPiece piece, int fromFile, int fromRank, int toFile, int toRank, GameState gameState);
}

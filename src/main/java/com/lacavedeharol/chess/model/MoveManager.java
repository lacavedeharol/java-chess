package com.lacavedeharol.chess.model;

import com.lacavedeharol.chess.model.validation.BishopMoveValidation;
import com.lacavedeharol.chess.model.validation.KingMoveValidation;
import com.lacavedeharol.chess.model.validation.KnightMoveValidation;
import com.lacavedeharol.chess.model.validation.MoveValidationStrategy;
import com.lacavedeharol.chess.model.validation.RookMoveValidation;
import com.lacavedeharol.chess.model.validation.QueenMoveValidation;
import com.lacavedeharol.chess.model.validation.PawnMoveValidation;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A component of the Model. Manages move validation strategies for different
 * piece types. It now depends on GameState, NOT ChessRenderer.
 */
public class MoveManager {

    private final GameState gameState;
    private final Map<PieceType, MoveValidationStrategy> validators;

    public MoveManager(GameState gameState) {
        this.gameState = gameState;
        this.validators = new HashMap<>();
        this.validators.put(PieceType.PAWN, new PawnMoveValidation());
        this.validators.put(PieceType.BISHOP, new BishopMoveValidation());
        this.validators.put(PieceType.KNIGHT, new KnightMoveValidation());
        this.validators.put(PieceType.ROOK, new RookMoveValidation());
        this.validators.put(PieceType.QUEEN, new QueenMoveValidation());
        this.validators.put(PieceType.KING, new KingMoveValidation());
    }

    public List<Point> generateLegalMoves(ChessPiece piece, int fromFile, int fromRank) {
        MoveValidationStrategy validator = validators.get(piece.getPieceType());
        if (validator == null) {
            return new ArrayList<>();
        }
        List<Point> moves = new ArrayList<>();
        for (int toFile = 0; toFile < 8; toFile++) {
            for (int toRank = 0; toRank < 8; toRank++) {
                if (validator.isValidMove(piece, fromFile, fromRank, toFile, toRank, gameState)) {
                    moves.add(new Point(toFile, toRank));
                }
            }
        }
        return moves;
    }

    public MoveValidationStrategy getValidator(PieceType pieceType) {
        return validators.get(pieceType);
    }
}

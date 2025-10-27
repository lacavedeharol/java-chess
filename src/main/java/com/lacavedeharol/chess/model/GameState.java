package com.lacavedeharol.chess.model;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.lacavedeharol.chess.model.validation.MoveValidationStrategy;

/**
 * The Model (GameState). Holds the complete state of the chess game and all the
 * core game logic.
 */
public class GameState {

    private GameRecorder gameRecorder;

    // Core State:
    private final ChessPiece[][] chessPieces;
    private final List<ChessPiece> capturedPieces;
    private boolean isWhiteToMove, isWhiteKingInCheck, isBlackKingInCheck;
    private Point enPassantTargetSquare;

    // Calculate valid moves for each piece.
    private final MoveManager moveManager;

    public GameState() {
        this.gameRecorder = new GameRecorder();
        this.chessPieces = new ChessPiece[8][8];
        this.capturedPieces = new ArrayList<>();
        this.isWhiteToMove = true;
        this.moveManager = new MoveManager(this);
        this.isWhiteKingInCheck = false;
        this.isBlackKingInCheck = false;
        initializePieces();
    }

    /**
     * Attempts to move a piece. This is the primary way to change the game
     * state.
     * 
     * @param fromFile
     * @param fromRank
     * @param toFile
     * @param toRank
     * @return true for a valid move, false otherwise.
     */
    public MoveResult movePiece(int fromFile, int fromRank, int toFile, int toRank) {
        // EN PASSANT: State Reset
        Point previousEnPassantTarget = enPassantTargetSquare;
        enPassantTargetSquare = null;

        // Basic Validation.
        ChessPiece piece = getPieceAt(fromFile, fromRank);
        if (piece == null || piece.isWhite() != isWhiteToMove) {
            return MoveResult.INVALID;
        }
        List<Point> legalMoves = getLegalMovesForPiece(fromFile, fromRank);
        if (!legalMoves.contains(new Point(toFile, toRank))) {
            return MoveResult.INVALID;
        }

        // Track if this was a capture BEFORE making the move.
        boolean wasCapture = false;
        boolean wasEnPassant = false;

        // Check for en passant capture.
        if (piece.getPieceType() == PieceType.PAWN &&
                new Point(toFile, toRank).equals(previousEnPassantTarget)) {
            wasCapture = true;
            wasEnPassant = true;
        } else if (getPieceAt(toFile, toRank) != null) {
            // Standard capture.
            wasCapture = true;
        }

        // CAPTURE LOGIC (Handles Standard vs. En Passant)
        if (piece.getPieceType() == PieceType.PAWN &&
                new Point(toFile, toRank).equals(previousEnPassantTarget)) {
            // This is an en passant capture. The captured pawn is on a different square.
            int capturedPawnRank = fromRank;
            int capturedPawnFile = toFile;
            ChessPiece capturedPawn = getPieceAt(capturedPawnFile, capturedPawnRank);
            if (capturedPawn != null) {
                capturedPieces.add(capturedPawn);
                chessPieces[capturedPawnFile][capturedPawnRank] = null;
            }
        } else {
            // This is a standard capture.
            ChessPiece captured = getPieceAt(toFile, toRank);
            if (captured != null) {
                capturedPieces.add(captured);
            }
        }

        // CASTLING LOGIC
        boolean wasCastling = false;
        if (piece.getPieceType() == PieceType.KING && Math.abs(toFile - fromFile) == 2) {
            wasCastling = true;
            if (toFile > fromFile) { // King-side
                ChessPiece rook = getPieceAt(7, fromRank);
                chessPieces[5][fromRank] = rook;
                chessPieces[7][fromRank] = null;
                rook.setPosition(5, fromRank);
                rook.markAsMoved();
            } else { // Queen-side
                ChessPiece rook = getPieceAt(0, fromRank);
                chessPieces[3][fromRank] = rook;
                chessPieces[0][fromRank] = null;
                rook.setPosition(3, fromRank);
                rook.markAsMoved();
            }
        }

        // PRIMARY PIECE MOVEMENT
        chessPieces[toFile][toRank] = piece;
        chessPieces[fromFile][fromRank] = null;
        piece.setPosition(toFile, toRank);
        piece.markAsMoved();

        // EN PASSANT: State Set for Next Turn
        if (piece.getPieceType() == PieceType.PAWN && Math.abs(fromRank - toRank) == 2) {
            enPassantTargetSquare = new Point(toFile, (fromRank + toRank) / 2);
        }

        boolean isPromotion = (piece.getPieceType() == PieceType.PAWN &&
                (toRank == 0 || toRank == 7));
        if (isPromotion) {
            // Record the move before promotion (promotion will be recorded separately).
            gameRecorder.recordMove(fromFile, fromRank, toFile, toRank, piece,
                    wasCapture, wasEnPassant, wasCastling, this);
            // Don't switch turns yet! The controller needs to get the promotion choice.
            return MoveResult.PROMOTION_REQUIRED;
        }

        // Record the successful move.
        gameRecorder.recordMove(fromFile, fromRank, toFile, toRank, piece,
                wasCapture, wasEnPassant, wasCastling, this);

        // FINAL STATE UPDATES: If the move is valid, change turn.
        isWhiteToMove = !isWhiteToMove;
        updateCheckStatus();
        return MoveResult.SUCCESS;
    }

    /**
     * Gets the list of legal moves for a piece at a given position. This is
     * useful for the Controller to know where a piece can go.
     *
     * @param file
     * @param rank
     * @return empty list if no piece or not their turn.
     */
    public List<Point> getLegalMovesForPiece(int file, int rank) {
        ChessPiece piece = getPieceAt(file, rank);
        if (piece == null || piece.isWhite() != isWhiteToMove) {
            return new ArrayList<>();
        }

        List<Point> pseudoLegalMoves = moveManager.generateLegalMoves(piece, file, rank);
        List<Point> legalMoves = new ArrayList<>();

        Point kingPosition = findKing(isWhiteToMove);
        if (kingPosition == null) {
            return pseudoLegalMoves;
        }

        for (Point move : pseudoLegalMoves) {
            ChessPiece capturedPiece = makeHypotheticalMove(file, rank, move.x, move.y);
            Point currentKingPos = (piece.getPieceType() == PieceType.KING) ? move : kingPosition;
            boolean kingIsInCheck = isSquareUnderAttack(currentKingPos.x, currentKingPos.y, !isWhiteToMove);
            undoHypotheticalMove(file, rank, move.x, move.y, piece, capturedPiece);

            if (!kingIsInCheck) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Replaces a pawn at a given square with a new piece of the player's choice.
     * This should only be called after a move results in PROMOTION_REQUIRED.
     *
     * @param file   The file of the pawn to be promoted.
     * @param rank   The rank of the pawn to be promoted.
     * @param choice The PieceType to promote the pawn to (e.g., QUEEN, ROOK).
     */
    public void promotePawn(int file, int rank, PieceType choice) {
        ChessPiece pawn = getPieceAt(file, rank);
        if (pawn == null || pawn.getPieceType() != PieceType.PAWN) {
            return;
        }

        ChessPiece promotedPiece = new ChessPiece(pawn.isWhite(), choice, file, rank);
        promotedPiece.markAsMoved();
        chessPieces[file][rank] = promotedPiece;

        // Record the promotion.
        gameRecorder.recordPromotion(choice);

        // Now that the move is fully complete, switch turns and update check status.
        isWhiteToMove = !isWhiteToMove;
        updateCheckStatus();
    }

    public ChessPiece getPieceAt(int file, int rank) {
        if (file < 0 || file >= 8 || rank < 0 || rank >= 8) {
            return null;
        }
        return chessPieces[file][rank];
    }

    public boolean isWhiteKingInCheck() {
        return isWhiteKingInCheck;
    }

    public boolean isBlackKingInCheck() {
        return isBlackKingInCheck;
    }

    public Point getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }

    public boolean isWhiteToMove() {
        return isWhiteToMove;
    }

    /**
     * Checks if a given square is under attack by the opponent.
     */
    public boolean isSquareUnderAttack(int file, int rank, boolean isAttackedByWhite) {
        for (int f = 0; f < 8; f++) {
            for (int r = 0; r < 8; r++) {
                ChessPiece piece = getPieceAt(f, r);
                if (piece != null && piece.isWhite() == isAttackedByWhite) {
                    MoveValidationStrategy validator = moveManager.getValidator(piece.getPieceType());
                    if (validator.isValidMove(piece, f, r, file, rank, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds the position of the specified color's king.
     */
    public Point findKing(boolean isWhite) {
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                ChessPiece piece = getPieceAt(f, r);
                if (piece != null && piece.getPieceType() == PieceType.KING &&
                        piece.isWhite() == isWhite) {
                    return new Point(f, r);
                }
            }
        }
        return null;
    }

    private void updateCheckStatus() {
        Point whiteKingPos = findKing(true);
        if (whiteKingPos != null) {
            this.isWhiteKingInCheck = isSquareUnderAttack(whiteKingPos.x, whiteKingPos.y, false);
        } else {
            this.isWhiteKingInCheck = false;
        }

        Point blackKingPos = findKing(false);
        if (blackKingPos != null) {
            this.isBlackKingInCheck = isSquareUnderAttack(blackKingPos.x, blackKingPos.y, true);
        } else {
            this.isBlackKingInCheck = false;
        }
    }

    /**
     * Temporarily performs a move on the board for validation purposes.
     */
    public ChessPiece makeHypotheticalMove(int fromFile, int fromRank, int toFile, int toRank) {
        ChessPiece movingPiece = getPieceAt(fromFile, fromRank);
        ChessPiece capturedPiece = getPieceAt(toFile, toRank);

        if (movingPiece.getPieceType() == PieceType.PAWN &&
                new Point(toFile, toRank).equals(this.enPassantTargetSquare)) {
            int capturedPawnFile = toFile;
            int capturedPawnRank = fromRank;
            capturedPiece = getPieceAt(capturedPawnFile, capturedPawnRank);
            chessPieces[capturedPawnFile][capturedPawnRank] = null;
        }

        chessPieces[toFile][toRank] = movingPiece;
        chessPieces[fromFile][fromRank] = null;

        return capturedPiece;
    }

    /**
     * Reverts a hypothetical move to restore the board to its original state.
     */
    public void undoHypotheticalMove(int fromFile, int fromRank, int toFile, int toRank,
            ChessPiece originalPiece, ChessPiece capturedPiece) {
        chessPieces[fromFile][fromRank] = originalPiece;

        if (originalPiece.getPieceType() == PieceType.PAWN &&
                new Point(toFile, toRank).equals(this.enPassantTargetSquare)) {
            chessPieces[toFile][toRank] = null;
            if (capturedPiece != null) {
                chessPieces[toFile][fromRank] = capturedPiece;
            }
        } else {
            chessPieces[toFile][toRank] = capturedPiece;
        }
    }

    private void initializePieces() {
        PieceType[] backRowOrder = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int file = 0; file < 8; file++) {
            chessPieces[file][6] = new ChessPiece(true, PieceType.PAWN, file, 6);
            chessPieces[file][1] = new ChessPiece(false, PieceType.PAWN, file, 1);
            chessPieces[file][7] = new ChessPiece(true, backRowOrder[file], file, 7);
            chessPieces[file][0] = new ChessPiece(false, backRowOrder[file], file, 0);
        }
    }

    /**
     * Determines the current status of the game (in progress, checkmate, or
     * stalemate).
     */
    public GameStatus getGameStatus() {
        if (hasLegalMoves(isWhiteToMove)) {
            return GameStatus.IN_PROGRESS;
        }

        if (isWhiteToMove) {
            if (isWhiteKingInCheck) {
                return GameStatus.CHECKMATE_BLACK_WINS;
            } else {
                return GameStatus.STALEMATE;
            }
        } else {
            if (isBlackKingInCheck) {
                return GameStatus.CHECKMATE_WHITE_WINS;
            } else {
                return GameStatus.STALEMATE;
            }
        }
    }

    /**
     * A helper method to check if a given side has any legal moves.
     */
    private boolean hasLegalMoves(boolean isWhite) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                ChessPiece piece = getPieceAt(file, rank);
                if (piece != null && piece.isWhite() == isWhite) {
                    if (!getLegalMovesForPiece(file, rank).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // GameRecorder Integration Methods

    /**
     * Set player names for the game record.
     * 
     * @param whiteName
     * @param blackName
     */
    public void setPlayerNames(String whiteName, String blackName) {
        gameRecorder.setPlayerNames(whiteName, blackName);
    }

    /**
     * Save the current game to a JSON file.
     * 
     * @param filepath
     * @throws IOException
     */
    public void saveGame(String filepath) throws IOException {
        gameRecorder.endGame(getGameStatus(), filepath);
    }

    /**
     * Get the game data for inspection or manual saving.
     * 
     * @return
     */
    public ChessGameData getGameData() {
        return gameRecorder.getGameData();
    }

    /**
     * Get the game data as a JSON string.
     * 
     * @return
     */
    public String getGameAsJson() {
        return gameRecorder.getGameData().toJson();
    }
}
package com.lacavedeharol.chess.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents a complete chess game with all moves and metadata.
 * Can be serialized to/from JSON for saving and loading games.
 */
public class ChessGameData {

    // Game Metadata:
    private String gameId;
    private String whitePlayerName;
    private String blackPlayerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GameStatus finalStatus;

    // Move History:
    private List<MoveRecord> moves;

    // Current game state (optional - for resuming games):
    private String currentFEN; // FEN notation for board state.
    private boolean isWhiteToMove;
    private int moveNumber;

    public ChessGameData() {
        this.gameId = generateGameId();
        this.moves = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.whitePlayerName = "White";
        this.blackPlayerName = "Black";
        this.moveNumber = 1;
        this.isWhiteToMove = true;
    }

    private String generateGameId() {
        return "GAME_" + System.currentTimeMillis();
    }

    public void addMove(MoveRecord move) {
        this.moves.add(move);
    }

    /**
     * Save game to JSON file.
     * 
     * @param filepath
     * @throws IOException
     */
    public void saveToFile(String filepath) throws IOException {
        this.endTime = LocalDateTime.now();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(this, writer);
        }
    }

    /**
     * Load game from JSON file.
     * 
     * @param filepath
     * @return
     * @throws IOException
     */
    public static ChessGameData loadFromFile(String filepath) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        try (FileReader reader = new FileReader(filepath)) {
            return gson.fromJson(reader, ChessGameData.class);
        }
    }

    // Convert to JSON string.
    public String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.toJson(this);
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public void setWhitePlayerName(String name) {
        this.whitePlayerName = name;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public void setBlackPlayerName(String name) {
        this.blackPlayerName = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public GameStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(GameStatus status) {
        this.finalStatus = status;
    }

    public List<MoveRecord> getMoves() {
        return moves;
    }

    public String getCurrentFEN() {
        return currentFEN;
    }

    public void setCurrentFEN(String fen) {
        this.currentFEN = fen;
    }

    public boolean isWhiteToMove() {
        return isWhiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove) {
        this.isWhiteToMove = whiteToMove;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    // Get game duration in minutes.
    public long getGameDurationMinutes() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return 0;
    }

    // Get total number of moves (half-moves).
    public int getTotalMoves() {
        return moves.size();
    }
}

/**
 * Represents a single move in the game.
 */
class MoveRecord {
    private int moveNumber;
    private boolean isWhiteMove;
    private String from; // e.g., "e2".
    private String to; // e.g., "e4".
    private PieceType pieceType;
    private boolean isCapture;
    private boolean isEnPassant;
    private boolean isCastling;
    private CastlingType castlingType;
    private boolean isPromotion;
    private PieceType promotedTo;
    private boolean isCheck;
    private boolean isCheckmate;
    private String algebraicNotation; // Standard chess notation like "Nf3" or "exd5".
    private LocalDateTime timestamp;

    public MoveRecord(int moveNumber, boolean isWhiteMove, int fromFile, int fromRank,
            int toFile, int toRank, PieceType pieceType) {
        this.moveNumber = moveNumber;
        this.isWhiteMove = isWhiteMove;
        this.from = coordinatesToAlgebraic(fromFile, fromRank);
        this.to = coordinatesToAlgebraic(toFile, toRank);
        this.pieceType = pieceType;
        this.timestamp = LocalDateTime.now();
        this.isCapture = false;
        this.isEnPassant = false;
        this.isCastling = false;
        this.isPromotion = false;
        this.isCheck = false;
        this.isCheckmate = false;
    }

    /**
     * Convert array coordinates to algebraic notation.
     * 
     * @param file
     * @param rank
     * @return
     */
    private String coordinatesToAlgebraic(int file, int rank) {
        char fileChar = (char) ('a' + file);
        int rankNum = 8 - rank; // Assuming rank 0 is top (rank 8 in chess).
        return "" + fileChar + rankNum;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public boolean isWhiteMove() {
        return isWhiteMove;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        this.isCapture = capture;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public void setEnPassant(boolean enPassant) {
        this.isEnPassant = enPassant;
    }

    public boolean isCastling() {
        return isCastling;
    }

    public void setCastling(boolean castling) {
        this.isCastling = castling;
    }

    public CastlingType getCastlingType() {
        return castlingType;
    }

    public void setCastlingType(CastlingType type) {
        this.castlingType = type;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        this.isPromotion = promotion;
    }

    public PieceType getPromotedTo() {
        return promotedTo;
    }

    public void setPromotedTo(PieceType promoted) {
        this.promotedTo = promoted;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        this.isCheck = check;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.isCheckmate = checkmate;
    }

    public String getAlgebraicNotation() {
        return algebraicNotation;
    }

    public void setAlgebraicNotation(String notation) {
        this.algebraicNotation = notation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return algebraicNotation != null ? algebraicNotation : (from + "-" + to);
    }
}

enum CastlingType {
    KINGSIDE, QUEENSIDE
}

/**
 * Custom adapter for LocalDateTime serialization with Gson
 */
class LocalDateTimeAdapter extends com.google.gson.TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(formatter));
        }
    }

    @Override
    public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return LocalDateTime.parse(in.nextString(), formatter);
    }
}

/**
 * Manages game recording and integrates with GameState.
 * Add this to your GameState class to automatically track moves.
 */
class GameRecorder {
    private ChessGameData gameData;
    private int fullMoveNumber;

    public GameRecorder() {
        this.gameData = new ChessGameData();
        this.fullMoveNumber = 1;
    }

    /**
     * Records a move. Call this from GameState.movePiece() after a successful move.
     * 
     * @param fromFile
     * @param fromRank
     * @param toFile
     * @param toRank
     * @param piece
     * @param wasCapture
     * @param wasEnPassant
     * @param wasCastling
     * @param gameState
     */
    public void recordMove(int fromFile, int fromRank, int toFile, int toRank,
            ChessPiece piece, boolean wasCapture, boolean wasEnPassant,
            boolean wasCastling, GameState gameState) {

        boolean isWhiteMove = piece.isWhite();
        MoveRecord move = new MoveRecord(fullMoveNumber, isWhiteMove,
                fromFile, fromRank, toFile, toRank,
                piece.getPieceType());

        // Set move flags
        move.setCapture(wasCapture);
        move.setEnPassant(wasEnPassant);

        if (wasCastling) {
            move.setCastling(true);
            move.setCastlingType(toFile > fromFile ? CastlingType.KINGSIDE : CastlingType.QUEENSIDE);
        }

        // Check status after move
        move.setCheck(isWhiteMove ? gameState.isBlackKingInCheck() : gameState.isWhiteKingInCheck());

        GameStatus status = gameState.getGameStatus();
        if (status == GameStatus.CHECKMATE_WHITE_WINS || status == GameStatus.CHECKMATE_BLACK_WINS) {
            move.setCheckmate(true);
        }

        gameData.addMove(move);

        // Increment move number after black's move
        if (!isWhiteMove) {
            fullMoveNumber++;
        }
    }

    /**
     * Records a pawn promotion. Call this after promotePawn() in GameState.
     * 
     * @param promotedTo
     */
    public void recordPromotion(PieceType promotedTo) {
        List<MoveRecord> moves = gameData.getMoves();
        if (!moves.isEmpty()) {
            MoveRecord lastMove = moves.get(moves.size() - 1);
            lastMove.setPromotion(true);
            lastMove.setPromotedTo(promotedTo);
        }
    }

    /**
     * Finalizes the game and saves it.
     * 
     * @param finalStatus
     * @param filepath
     * @throws IOException
     */
    public void endGame(GameStatus finalStatus, String filepath) throws IOException {
        gameData.setFinalStatus(finalStatus);
        gameData.saveToFile(filepath);
    }

    public ChessGameData getGameData() {
        return gameData;
    }

    public void setPlayerNames(String whiteName, String blackName) {
        gameData.setWhitePlayerName(whiteName);
        gameData.setBlackPlayerName(blackName);
    }
}
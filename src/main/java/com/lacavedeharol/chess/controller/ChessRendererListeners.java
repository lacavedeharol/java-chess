package com.lacavedeharol.chess.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.lacavedeharol.chess.model.SimpleAI;
import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;
import com.lacavedeharol.chess.model.GameStatus;
import com.lacavedeharol.chess.model.MoveResult;
import com.lacavedeharol.chess.model.PieceType;
import com.lacavedeharol.chess.view.ChessRenderer;
import com.lacavedeharol.chess.view.PromotionDialog;

/**
 * The Controller. Translates user input (mouse clicks/drags) into actions for
 * the Model. It holds references to both the Model (GameState) and the View
 * (ChessRenderer).
 */
public class ChessRendererListeners implements MouseListener, MouseMotionListener {

    private final GameState gameState;
    private final ChessRenderer chessRenderer;
    private final SimpleAI ai; // Can be null for two-player mode
    private final boolean isTwoPlayerMode;

    private ChessPiece selectedPiece;
    private int fromFile, fromRank;
    private List<Point> legalMoves;

    private Map<String, ImageIcon> promotionIcons;

    public ChessRendererListeners(GameState gameState, ChessRenderer chessRenderer, SimpleAI ai) {
        this.gameState = gameState;
        this.chessRenderer = chessRenderer;
        this.ai = ai;
        this.isTwoPlayerMode = (ai == null);
        this.chessRenderer.addMouseListener(this);
        this.chessRenderer.addMouseMotionListener(this);

        this.legalMoves = new ArrayList<>();

        createPromotionIcons();

    }

    public void startGame() {
        handleNextTurn();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // In AI mode, only allow white moves.
        if (!isTwoPlayerMode && !gameState.isWhiteToMove())
            return;

        Point startPoint = getSquareFromMouseEvent(e);
        if (startPoint == null) {
            return;
        }

        int file = startPoint.x;
        int rank = startPoint.y;
        ChessPiece piece = gameState.getPieceAt(file, rank);
        // Allow selecting pieces matching the current turn's color.
        if (piece != null && piece.isWhite() == gameState.isWhiteToMove()) {
            selectedPiece = piece;
            fromFile = file;
            fromRank = rank;
            legalMoves = gameState.getLegalMovesForPiece(file, rank);
            chessRenderer.getLegalMovePainter().setMarkerLocations(legalMoves);
            chessRenderer.setDraggedPiece(selectedPiece);
            chessRenderer.setDraggedPieceSquare(fromFile, fromRank);
            chessRenderer.repaint();
        }
        chessRenderer.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPiece == null)
            return;

        Point hoverPoint = getSquareFromMouseEvent(e);
        if (hoverPoint != null && legalMoves.contains(hoverPoint)) {
            chessRenderer.setDraggedPieceSquare(hoverPoint.x, hoverPoint.y);
        } else {
            chessRenderer.setDraggedPieceSquare(fromFile, fromRank);
        }
        chessRenderer.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedPiece == null)
            return;

        Point releasePoint = getSquareFromMouseEvent(e);
        int toFile = (releasePoint != null) ? releasePoint.x : -1;
        int toRank = (releasePoint != null) ? releasePoint.y : -1;

        // Store the result of the move.
        MoveResult result = gameState.movePiece(fromFile, fromRank, toFile, toRank);

        selectedPiece = null;
        legalMoves.clear();
        chessRenderer.getLegalMovePainter().setMarkerLocations(new ArrayList<>());
        chessRenderer.setDraggedPiece(null);
        chessRenderer.repaint();

        switch (result) {
            case MoveResult.SUCCESS -> {
                if (!checkGameStatus()) {
                    handleNextTurn();
                }
            }
            case MoveResult.PROMOTION_REQUIRED -> {
                handlePromotion(toFile, toRank);
            }
            case MoveResult.INVALID -> {
                // Do nothing.
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private Point getSquareFromMouseEvent(MouseEvent e) {
        int squareLength = chessRenderer.getBoardSquareLength();

        int file = (e.getX() - squareLength * 2) / squareLength;
        int rank = (e.getY() - squareLength * 2) / squareLength;

        if (file < 0 || file >= 8 || rank < 0 || rank >= 8) {
            return null;
        }
        return new Point(file, rank);
    }

    /**
     * This method displays the pawn promotion dialog.
     * 
     * @param file
     * @param rank
     */
    private void handlePromotion(int file, int rank) {
        // We need to know if the pawn is white to show the correct colored icons.
        boolean isWhite = (rank == 0);

        Map<String, ImageIcon> iconsForDialog = new HashMap<>();

        PieceType[] pieces = { PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT };
        String colorKey = isWhite ? "_WHITE" : "_BLACK";
        for (PieceType piece : pieces) {
            iconsForDialog.put(piece.name() + colorKey, promotionIcons.get(piece.name() + colorKey));
        }

        // Create and show the custom dialog.
        PromotionDialog dialog = new PromotionDialog((JFrame) SwingUtilities.getWindowAncestor(chessRenderer),
                iconsForDialog);
        dialog.setVisible(true);

        PieceType selectedPieceType = dialog.getSelectedPiece();

        // If the user closed the dialog without choosing, default to Queen.
        if (selectedPieceType == null) {
            selectedPieceType = PieceType.QUEEN;
        }

        gameState.promotePawn(file, rank, selectedPieceType);

        chessRenderer.repaint();
        if (!checkGameStatus()) {
            handleNextTurn();
        }
    }

    /**
     * Checks the game status and ends the game if necessary.
     *
     * @return true if the game is over, false otherwise.
     */
    private boolean checkGameStatus() {
        GameStatus status = gameState.getGameStatus();
        if (status == GameStatus.IN_PROGRESS) {
            return false;
        }

        String message;
        switch (status) {
            case CHECKMATE_WHITE_WINS -> message = "Checkmate! White wins.";
            case CHECKMATE_BLACK_WINS -> message = "Checkmate! Black wins.";
            case STALEMATE -> message = "Stalemate! The game is a draw.";
            default -> throw new IllegalStateException("Unexpected game status: " + status);
        }

        // Show the game over message.
        JOptionPane.showMessageDialog(chessRenderer, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        // Disable listeners to prevent further moves.
        chessRenderer.removeMouseListener(this);
        chessRenderer.removeMouseMotionListener(this);

        return true; // Game is over.
    }

    private void createPromotionIcons() {
        promotionIcons = new HashMap<>();
        BufferedImage spriteSheet = ChessPiece.getSpriteSheet();
        int spriteSize = 16;

        PieceType[] pieceTypes = { PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT };
        int[] pieceXCoords = { 4, 3, 1, 2 }; // Queen, Rook, Bishop, Knight

        for (int i = 0; i < pieceTypes.length; i++) {
            PieceType type = pieceTypes[i];
            int x = pieceXCoords[i] * spriteSize;

            // White pieces (top row).
            BufferedImage whiteSprite = spriteSheet.getSubimage(x, 0, spriteSize, spriteSize * 2);
            promotionIcons.put(type.name() + "_WHITE", new ImageIcon(whiteSprite));

            // Black pieces (third row).
            BufferedImage blackSprite = spriteSheet.getSubimage(x, spriteSize * 2, spriteSize, spriteSize * 2);
            promotionIcons.put(type.name() + "_BLACK", new ImageIcon(blackSprite));
        }
    }

    /**
     * Checks if it's the AI's turn and, if so, triggers its move.
     */
    private void handleNextTurn() {
        if (isTwoPlayerMode) {
            /*
             * In two-player mode, just update the display and wait for the next player's
             * move.
             */
            chessRenderer.repaint();
            return;
        }

        // AI mode logic.
        if (gameState.isWhiteToMove() == ai.isWhite()) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Slight delay, i am going for a "natural" feel.
                    Thread.sleep(1000);
                    ai.makeMove(gameState);
                    return null;
                }

                @Override
                protected void done() {
                    chessRenderer.repaint();
                    // After the AI moves, check again in case of checkmate/stalemate.
                    checkGameStatus();
                }
            }.execute();
        }
        // If it's not the AI's turn, do nothing and wait for mouse input.
    }

}

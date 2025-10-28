package com.lacavedeharol.chess.view;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import com.lacavedeharol.chess.model.GameState;

public class CheckIndicatorPainter {

    private final GameState gameState;
    private final ChessRenderer chessRenderer;
    private BufferedImage checkIndicatorSprite;

    private Timer animationTimer;
    private float yOffset = 0f;
    private boolean isMovingUp = true, isAnimating = false, wasBlackInCheck = false, wasWhiteInCheck = false;
    private int bounceCount = 0;
    private Point kingAnimationPos = null;

    private final int MAX_BOUNCES = 3;
    private final int MAX_OFFSET = 8;
    private final float ANIMATION_SPEED = 1.0f;

    public CheckIndicatorPainter(GameState gameState, ChessRenderer chessRenderer) {
        this.gameState = gameState;
        this.chessRenderer = chessRenderer;
        loadSprite();
        setupAnimationTimer();
    }

    private void loadSprite() {
        try {
            checkIndicatorSprite = ImageIO.read(getClass().getResourceAsStream("/images/check_indicator.png"));
        } catch (IOException e) {
        }
    }

    private void setupAnimationTimer() {
        animationTimer = new Timer(15, e -> {
            if (!isAnimating)
                return;

            if (isMovingUp) {
                yOffset += ANIMATION_SPEED;
                if (yOffset >= MAX_OFFSET) {
                    yOffset = MAX_OFFSET;
                    isMovingUp = false;
                }
            } else {
                yOffset -= ANIMATION_SPEED;
                if (yOffset <= 0) {
                    yOffset = 0;
                    isMovingUp = true;
                    bounceCount++;
                }
            }

            if (bounceCount >= MAX_BOUNCES) {
                isAnimating = false;
                animationTimer.stop();
            }

            chessRenderer.repaint();
        });
    }

    /**
     * Draws the check indicator over the king if it's in check.
     * 
     * @param g2d
     * @param boardSquareSize
     */
    public void draw(Graphics2D g2d, int boardSquareSize) {
        boolean isNewWhiteCheck = gameState.isWhiteKingInCheck() && !wasWhiteInCheck;
        boolean isNewBlackCheck = gameState.isBlackKingInCheck() && !wasBlackInCheck;

        if (isNewWhiteCheck || isNewBlackCheck) {
            // If a new check is detected, start the animation
            isAnimating = true;
            bounceCount = 0;
            yOffset = 0;
            isMovingUp = true;
            kingAnimationPos = isNewWhiteCheck ? gameState.findKing(true) : gameState.findKing(false);
            animationTimer.start();
        }
        wasWhiteInCheck = gameState.isWhiteKingInCheck();
        wasBlackInCheck = gameState.isBlackKingInCheck();
        if (isAnimating && kingAnimationPos != null && checkIndicatorSprite != null) {
            int x = kingAnimationPos.x * boardSquareSize;
            int y = (int) (kingAnimationPos.y * boardSquareSize - yOffset);
            g2d.drawImage(checkIndicatorSprite, x, y, boardSquareSize, boardSquareSize, null);
        }
    }
}
package com.lacavedeharol.chess.model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A Model class. Represents the data of a single chess piece.
 * 
 */
public class ChessPiece {

    private static BufferedImage chessPiecesSpriteSheet, chessPieceShadow, promotionIconsSpriteSheet;

    static {
        try {
            chessPiecesSpriteSheet = ImageIO.read(ChessPiece.class.getResourceAsStream("/images/chessPieces.png"));
            chessPieceShadow = ImageIO.read(ChessPiece.class.getResourceAsStream("/images/chessPieceShadow.png"));
            promotionIconsSpriteSheet = ImageIO
                    .read(ChessPiece.class.getResourceAsStream("/images/promotionPieceChooser.png"));
        } catch (IOException ex) {
        }
    }

    // Core Data:
    private final boolean isWhite;
    private boolean hasMoved;
    private final PieceType pieceType;
    private int file, rank;

    public ChessPiece(boolean isWhite, PieceType pieceType, int file, int rank) {
        this.isWhite = isWhite;
        this.pieceType = pieceType;
        this.file = file;
        this.rank = rank;
        this.hasMoved = false;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public static BufferedImage getSpriteSheet() {
        return chessPiecesSpriteSheet;
    }

    public static BufferedImage getPieceShadow() {
        return chessPieceShadow;
    }

    public static BufferedImage getPromotionIconsSpriteSheet() {
        return promotionIconsSpriteSheet;
    }

    public void setPosition(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    public void markAsMoved() {
        this.hasMoved = true;
    }
}

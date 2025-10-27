package com.lacavedeharol.chess.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ChessBoard {

    private static final int SPRITE_SIZE = 16;
    private static BufferedImage boardSquares;

    static {
        try (InputStream is = ChessBoard.class.getResourceAsStream("/images/chessBoardSquares.png")) {
            boardSquares = ImageIO.read(is);
        } catch (IOException ex) {
        }
    }

    public ChessBoard() {
    }

    public void draw(Graphics2D g2d, int squareWidth) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                int spriteX = isLightSquare(file, rank) ? 0 : SPRITE_SIZE;
                BufferedImage squareImage = boardSquares.getSubimage(spriteX, 0, SPRITE_SIZE,
                        SPRITE_SIZE);
                g2d.drawImage(squareImage, file * squareWidth, rank * squareWidth,
                        squareWidth, squareWidth, null);
                // g2d.drawRect(file * squareWidth, rank * squareHeight, squareWidth,
                // squareHeight);
            }
        }
    }

    private boolean isLightSquare(int file, int rank) {
        return (file + rank) % 2 == 0;
    }

}

package com.lacavedeharol.chess.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class BorderPainter {

        private static final int SPRITE_SIZE = 16;
        private static BufferedImage boardBorder; //

        static {
                try (InputStream is = ChessBoard.class.getResourceAsStream("/images/chess_board_border.png")) {
                        boardBorder = ImageIO.read(is);
                } catch (IOException ex) {
                }
        }

        public void draw(Graphics2D g2d, int boardSquareLength) {

                g2d.translate(boardSquareLength, boardSquareLength);
                for (int x = 1; x < 9; x++) {
                        g2d.drawImage(boardBorder.getSubimage(16, 0, SPRITE_SIZE, SPRITE_SIZE),
                                        x * boardSquareLength,
                                        0 * boardSquareLength,
                                        boardSquareLength, boardSquareLength, null);
                        g2d.drawImage(boardBorder.getSubimage(16, 32, SPRITE_SIZE, SPRITE_SIZE),
                                        x * boardSquareLength,
                                        9 * boardSquareLength,
                                        boardSquareLength, boardSquareLength, null);
                        for (int y = 1; y < 9; y++) {
                                g2d.drawImage(boardBorder.getSubimage(0, 16, SPRITE_SIZE, SPRITE_SIZE),
                                                0 * boardSquareLength,
                                                y * boardSquareLength,
                                                boardSquareLength, boardSquareLength, null);
                                g2d.drawImage(boardBorder.getSubimage(32, 16, SPRITE_SIZE, SPRITE_SIZE),
                                                9 * boardSquareLength,
                                                y * boardSquareLength,
                                                boardSquareLength, boardSquareLength, null);
                        }
                        g2d.drawImage(boardBorder.getSubimage(0, 0, SPRITE_SIZE, SPRITE_SIZE),
                                        0 * boardSquareLength,
                                        0 * boardSquareLength,
                                        boardSquareLength, boardSquareLength, null);
                        g2d.drawImage(boardBorder.getSubimage(32, 0, SPRITE_SIZE, SPRITE_SIZE),
                                        9 * boardSquareLength,
                                        0 * boardSquareLength,
                                        boardSquareLength, boardSquareLength, null);
                        g2d.drawImage(boardBorder.getSubimage(0, 32, SPRITE_SIZE, SPRITE_SIZE),
                                        0 * boardSquareLength,
                                        9 * boardSquareLength,
                                        boardSquareLength, boardSquareLength, null);
                        g2d.drawImage(boardBorder.getSubimage(32, 32, SPRITE_SIZE, SPRITE_SIZE),
                                        9 * boardSquareLength,
                                        9 * boardSquareLength,
                                        boardSquareLength, boardSquareLength, null);
                }
                g2d.translate(-boardSquareLength, -boardSquareLength);

        }
}

package com.lacavedeharol.chess.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.GameState;
import com.lacavedeharol.chess.model.PieceType;

public class ChessRenderer extends JPanel implements ComponentListener {

    private static final int SPRITE_SIZE = 16;
    private int boardSquareLength = 64;
    private final GameState gameState;
    private final ChessBoard chessBoard;
    private final BorderPainter borderPainter;
    // private final CheckIndicatorPainter checkIndicatorPainter;
    private final LegalMovePainter legalMovePainter;
    private ChessPiece draggedPiece;
    private int draggedFile, draggedRank;

    public ChessRenderer(GameState gameState) {
        this.gameState = gameState;
        this.chessBoard = new ChessBoard();
        this.borderPainter = new BorderPainter();
        // this.checkIndicatorPainter = new CheckIndicatorPainter(gameState, this);
        this.legalMovePainter = new LegalMovePainter();
        this.addComponentListener(this);

        setLayout(new BorderLayout());
        JPopupMenu optionsMenu = new JPopupMenu();
        JMenuItem restartItem = new JMenuItem("Restart Game");
        JMenuItem settingsItem = new JMenuItem("Settings...");
        JMenuItem quitItem = new JMenuItem("Quit");
        optionsMenu.add(restartItem);
        optionsMenu.add(settingsItem);
        optionsMenu.addSeparator();
        optionsMenu.add(quitItem);

        JButton optionsButton = new JButton("gear-icon");
        styleMenuButton(optionsButton);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setOpaque(false);
        headerPanel.add(optionsButton);

        this.add(headerPanel, BorderLayout.NORTH);

        optionsButton.addActionListener(e -> {
            optionsMenu.show(optionsButton, 0, optionsButton.getHeight());
        });

        restartItem.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            com.lacavedeharol.chess.Main.main(null);
        });

        settingsItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Settings dialog coming soon!");
        });

        quitItem.addActionListener(e -> System.exit(0));
    }

    private void styleMenuButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setColor(Color.decode("#241112"));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // debug print
        g2d.setColor(Color.BLACK);
        int x = 0;
        while (x < getWidth()) {
            int y = 0;
            while (y < getHeight()) {
                // g2d.drawRect(x, y, boardSquareLength, boardSquareLength);
                y += boardSquareLength;

            }
            x += boardSquareLength;
        }
        // end of debug print
        borderPainter.draw(g2d, boardSquareLength);
        g2d.translate(boardSquareLength * 2, boardSquareLength * 2);

        chessBoard.draw(g2d, boardSquareLength);

        legalMovePainter.draw(g2d, boardSquareLength);
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {

                // Is the currently dragged piece supposed to be on this square?
                if (draggedPiece != null && file == draggedFile && rank == draggedRank) {

                    BufferedImage shadowImage = ChessPiece.getPieceShadow();
                    int shadowWidth = boardSquareLength;
                    int shadowHeight = boardSquareLength * 2;
                    int shadowX = draggedFile * boardSquareLength;
                    int shadowY = draggedRank * boardSquareLength - (boardSquareLength / 2 - boardSquareLength / 16);
                    g2d.drawImage(shadowImage, shadowX, shadowY, shadowWidth,
                            shadowHeight, null);

                    BufferedImage pieceImage = getPieceImage(draggedPiece);
                    int pieceWidth = boardSquareLength;
                    int pieceHeight = boardSquareLength * 2;
                    int pieceX = draggedFile * boardSquareLength;
                    int pieceY = draggedRank * boardSquareLength
                            - (boardSquareLength + boardSquareLength / 2 - boardSquareLength / 16)
                            - boardSquareLength / 8;
                    g2d.drawImage(pieceImage, pieceX, pieceY, pieceWidth, pieceHeight, null);

                } else {

                    ChessPiece piece = gameState.getPieceAt(file, rank);
                    if (piece != null && piece != draggedPiece) {
                        drawPiece(g2d, piece);
                    }
                }
            }
        }
        g2d.translate(-boardSquareLength * 2, -boardSquareLength * 2);
    }

    private void drawPiece(Graphics2D g2d, ChessPiece piece) {
        // Draw the Shadow
        BufferedImage shadowImage = ChessPiece.getPieceShadow();
        int shadowWidth = boardSquareLength;
        int shadowHeight = boardSquareLength * 2;
        int shadowX = piece.getFile() * boardSquareLength;
        int shadowY = piece.getRank() * boardSquareLength - (boardSquareLength / 2 - boardSquareLength / 16);
        g2d.drawImage(shadowImage, shadowX, shadowY, shadowWidth, shadowHeight,
                null);
        // Draw the Piece
        BufferedImage pieceImage = getPieceImage(piece);
        int pieceWidth = boardSquareLength;
        int pieceHeight = boardSquareLength * 2;
        int pieceX = piece.getFile() * boardSquareLength;
        int pieceY = piece.getRank() * boardSquareLength
                - (boardSquareLength + boardSquareLength / 2 - boardSquareLength / 16);
        g2d.drawImage(pieceImage, pieceX, pieceY, pieceWidth, pieceHeight, null);
    }

    private BufferedImage getPieceImage(ChessPiece piece) {
        Map<PieceType, Integer> pieceXCoords = Map.of(
                PieceType.PAWN, 0,
                PieceType.BISHOP, 1,
                PieceType.KNIGHT, 2,
                PieceType.ROOK, 3,
                PieceType.QUEEN, 4,
                PieceType.KING, 5);

        int spriteX = pieceXCoords.get(piece.getPieceType()) * SPRITE_SIZE;
        int spriteY = piece.isWhite() ? 0 : SPRITE_SIZE * 2;

        int spriteWidth = SPRITE_SIZE;
        int spriteHeight = SPRITE_SIZE * 2;

        return ChessPiece.getSpriteSheet().getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
    }

    public void setDraggedPiece(ChessPiece piece) {
        this.draggedPiece = piece;
    }

    public void setDraggedPieceSquare(int file, int rank) {
        this.draggedFile = file;
        this.draggedRank = rank;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(boardSquareLength * 12, boardSquareLength * 12);
    }

    public int getBoardSquareLength() {
        return this.boardSquareLength;
    }

    public LegalMovePainter getLegalMovePainter() {
        return this.legalMovePainter;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        boardSquareLength = Math.min(getWidth(), getHeight()) / 12;
        revalidate();
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}

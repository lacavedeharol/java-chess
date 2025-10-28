package com.lacavedeharol.chess.view.components;

import com.lacavedeharol.chess.model.ChessPiece;
import com.lacavedeharol.chess.model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class PromotionDialog extends JDialog implements ActionListener {

    private PieceType selectedPiece = null;

    /**
     * Constructor for the custom promotion dialog.
     *
     * @param parent The parent frame, to center the dialog over it.
     * @param icons  A map of icons to display.
     */
    public PromotionDialog(Frame parent, boolean isWhite) {
        super(parent, "Pawn Promotion", true);
        setUndecorated(false);
        setLayout(new GridLayout(1, 4, 0, 0));
        getContentPane().setBackground(Color.decode("#a9a499"));

        BufferedImage spriteSheet = ChessPiece.getPromotionIconsSpriteSheet();
        int spriteSize = 16;
        for (int i = 0; i < PieceType.values().length - 2; i++) {
            ChessButton button = new ChessButton(
                    spriteSheet.getSubimage(i * spriteSize, isWhite ? 0 : spriteSize, spriteSize, spriteSize),
                    PieceType.values()[i + 1]);
            button.addActionListener(this);
            add(button);
        }

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * Public method for the controller to retrieve the user's choice.
     *
     * @return The PieceType chosen, or null if the dialog was closed without a
     *         choice.
     */
    public PieceType getSelectedPiece() {
        return selectedPiece;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ChessButton button = (ChessButton) e.getSource();
        selectedPiece = button.getPieceType();
        dispose();
    }
}

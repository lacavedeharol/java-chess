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
        getContentPane().setBackground(Color.decode("#846964"));

        BufferedImage spriteSheet = ChessPiece.getPromotionIconsSpriteSheet();
        int spriteSize = 16;
        int x = 0;
        for (String i : "4,3,1,2".split(",")) {

            PromotionButton button = new PromotionButton(
                    spriteSheet.getSubimage(x * spriteSize, isWhite ? 0 : spriteSize, spriteSize, spriteSize),
                    PieceType.values()[Integer.valueOf(i)]);
            button.addActionListener(this);
            add(button);
            x++;
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
        PromotionButton button = (PromotionButton) e.getSource();
        selectedPiece = button.getPieceType();
        dispose();
    }
}

package com.lacavedeharol.chess.view;

import com.lacavedeharol.chess.model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PromotionDialog extends JDialog {

    private PieceType selectedPiece = null;

    /**
     * Constructor for the custom promotion dialog.
     *
     * @param parent The parent frame, to center the dialog over it.
     * @param icons  A map of icons to display.
     */
    public PromotionDialog(Frame parent, Map<String, ImageIcon> icons) {
        super(parent, "Pawn Promotion", true); // true for modal

        // Basic setup
        setUndecorated(false); // Removes the title bar and border
        setLayout(new GridLayout(1, 4, 16, 16)); // 1 row, 4 columns
        getContentPane().setBackground(Color.decode("#241112"));

        PieceType[] pieces = { PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT };
        String colorKey = icons.containsKey("QUEEN_WHITE") ? "_WHITE" : "_BLACK";

        for (PieceType piece : pieces) {
            JButton button = createPromotionButton(icons.get(piece.name() + colorKey), piece);
            add(button);
        }

        pack();
        setLocationRelativeTo(parent); // Center on the parent window
    }

    /**
     * Helper method to create a styled button for the dialog.
     */
    private JButton createPromotionButton(ImageIcon icon, PieceType pieceType) {
        JButton button = new JButton(icon);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // When the button is clicked, store the choice and close the dialog.
        button.addActionListener(e -> {
            selectedPiece = pieceType;
            dispose(); // This closes the dialog
        });
        return button;
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
}

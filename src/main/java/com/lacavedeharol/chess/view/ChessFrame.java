package com.lacavedeharol.chess.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChessFrame extends JFrame {

    public ChessFrame(JPanel mainPanel) {
        add(mainPanel);
        pack();
        setTitle("Java Chess");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}

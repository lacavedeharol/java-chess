package com.lacavedeharol.chess.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    public MainFrame(JPanel mainPanel) {
        add(mainPanel);
        pack();
        setTitle("Java Chess");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

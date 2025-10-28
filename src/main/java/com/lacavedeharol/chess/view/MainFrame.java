package com.lacavedeharol.chess.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    public MainFrame(JPanel mainPanel) {
        setLayout(new BorderLayout(0, 0));
        add(mainPanel, "Center");
        pack();
        setMinimumSize(getPreferredSize());
        setTitle("Java Chess");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

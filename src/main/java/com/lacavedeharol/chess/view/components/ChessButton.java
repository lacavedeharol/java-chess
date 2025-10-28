package com.lacavedeharol.chess.view.components;

import javax.swing.*;

import com.lacavedeharol.chess.model.PieceType;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChessButton extends JComponent implements MouseListener {

    private Image image;
    private PieceType pieceType;
    private boolean pressed;

    private List<ActionListener> actionListeners = new ArrayList<>();

    public ChessButton(Image image, PieceType pieceType) {
        this.image = image;
        this.pieceType = pieceType;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(image, pressed ? 2 : 0, pressed ? 2 : 0, pressed ? 60 : 64, pressed ? 60 : 64, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(64, 64);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        this.repaint();
        fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, pieceType.name()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Adds an ActionListener to the button.
     * 
     * @param listener The ActionListener to add
     */
    public void addActionListener(ActionListener listener) {
        if (listener != null && !actionListeners.contains(listener)) {
            actionListeners.add(listener);
        }
    }

    /**
     * Removes an ActionListener from the button.
     * 
     * @param listener The ActionListener to remove
     */
    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    /**
     * Notifies all registered ActionListeners that an action has been performed.
     * 
     * @param event The ActionEvent to fire.
     */
    protected void fireActionPerformed(ActionEvent event) {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }

    /**
     * Public method for the controller to retrieve the user's choice.
     * 
     * @return The PieceType chosen, or null if the dialog was closed without a
     *         choice.
     */
    public PieceType getPieceType() {
        return pieceType;
    }
}
package com.lacavedeharol.chess;

import javax.swing.SwingUtilities;

import com.lacavedeharol.chess.controller.ChessRendererListeners;
import com.lacavedeharol.chess.model.GameState;
import com.lacavedeharol.chess.model.SimpleAI;
import com.lacavedeharol.chess.view.ChessRenderer;
import com.lacavedeharol.chess.view.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState gameState = new GameState();
            ChessRenderer chessRenderer = new ChessRenderer(gameState);
            SimpleAI ai = new SimpleAI(false);
            ChessRendererListeners controller = new ChessRendererListeners(gameState, chessRenderer, null);

            new MainFrame(chessRenderer);

            controller.startGame();
        });
    }
}
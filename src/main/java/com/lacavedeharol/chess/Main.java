package com.lacavedeharol.chess;

import javax.swing.SwingUtilities;

import com.lacavedeharol.chess.controller.ChessRendererListeners;
import com.lacavedeharol.chess.model.GameState;
import com.lacavedeharol.chess.model.SimpleAI;
import com.lacavedeharol.chess.view.ChessRenderer;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState gameState;
            ChessRenderer chessRenderer;
            ChessRendererListeners controller;
            gameState = new GameState();
            chessRenderer = new ChessRenderer(gameState);
            controller = new ChessRendererListeners(gameState, chessRenderer,
                    null);// new SimpleAI(false)
            controller.startGame();
        });
    }
}
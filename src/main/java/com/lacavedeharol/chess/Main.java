package com.lacavedeharol.chess;

import javax.swing.SwingUtilities;

import com.lacavedeharol.chess.controller.ChessRendererListeners;
import com.lacavedeharol.chess.model.GameState;
import com.lacavedeharol.chess.model.ImprovedAI;
import com.lacavedeharol.chess.view.ChessRenderer;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState gameState;
            ChessRenderer chessRenderer;
            ChessRendererListeners controller;
            gameState = new GameState();
            chessRenderer = new ChessRenderer(gameState);
            /**
             * For single-player mode against AI, instantiate ImprovedAI and pass it to the
             * controller.
             * For two-player mode, pass null instead.
             */
            controller = new ChessRendererListeners(gameState, chessRenderer,
                    new ImprovedAI(false));
            controller.startGame();
        });
    }
}
package com.lacavedeharol.chess.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RenderingUtilities {

    private RenderingUtilities() {
    }

    public static Color dark, light, reddish, redder;

    static {
        dark = Color.decode("#231e22");
        light = Color.decode("#a9a499");
        reddish = Color.decode("#b8583b");
        redder = Color.decode("#953a1b");
    }

    public static Graphics2D preparePixelArtGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // Disable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        // Disable text anti-aliasing (if you use bitmap fonts)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // Force nearest-neighbor scaling
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Disable fractional metrics for consistent pixel alignment
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        // Optional: disable dithering
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        // Optional: set rendering quality low to avoid extra smoothing
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        return g2d;
    }
}

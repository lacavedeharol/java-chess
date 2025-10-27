package com.lacavedeharol.chess.view;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class LegalMovePainter {

    private Image markerSprite;
    private List<Point> markerLocations;

    public LegalMovePainter() {
        this.markerLocations = new ArrayList<>();
        loadSprite();
    }

    private void loadSprite() {
        try {
            markerSprite = ImageIO.read(getClass().getResourceAsStream("/images/legalMoveMarker.png"));
        } catch (IOException e) {
        }
    }

    /**
     * Updates the list of squares where markers should be drawn.
     * 
     * @param locations A list of points (file, rank).
     */
    public void setMarkerLocations(List<Point> locations) {
        this.markerLocations = locations;
    }

    /**
     * Draws the markers on the board.
     */
    public void draw(Graphics2D g2d, int boardSquareLength) {
        if (markerSprite == null || markerLocations.isEmpty()) {
            return;
        }

        for (Point pos : markerLocations) {
            g2d.drawImage(markerSprite,
                    pos.x * boardSquareLength,
                    pos.y * boardSquareLength,
                    boardSquareLength,
                    boardSquareLength,
                    null);
        }
    }
}

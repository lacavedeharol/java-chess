package com.lacavedeharol.chess.view.components;

import java.awt.Color;

public class Utilities {

    private Utilities() {
    }

    public static Color dark, light, reddish, redder;

    static {
        dark = Color.decode("#231e22");
        light = Color.decode("#a9a499");
        reddish = Color.decode("#b8583b");
        redder = Color.decode("#953a1b");
    }
}

package com.tetris.view;

import java.awt.Color;
import java.awt.Font;

public final class ColorTheme {

    private ColorTheme() {
    }

    public static final Color SIDE_PANEL_BACKGROUND = new Color(30, 30, 30);
    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_ACCENT = new Color(0, 240, 240);
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 160);
    public static final Color BLOCK_BORDER_COLOR = new Color(255, 255, 255, 90);
    public static final Color NEXT_BOX_BACKGROUND = new Color(20, 20, 20);

    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font FONT_OVERLAY = new Font("SansSerif", Font.BOLD, 32);
}

package com.tetris.view;

import com.tetris.constants.Constants;
import com.tetris.controller.GameController;
import com.tetris.model.GameState;
import com.tetris.model.TetrominoType;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public final class SidePanel extends JPanel {

    private static final int NEXT_BOX_SIZE = 4 * Constants.CELL_SIZE;
    private static final int MARGIN = 20;

    private final GameController controller;
    private final GameState gameState;

    public SidePanel(GameController controller, GameState gameState) {
        this.controller = controller;
        this.gameState = gameState;
        setPreferredSize(new Dimension(Constants.SIDE_PANEL_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(ColorTheme.SIDE_PANEL_BACKGROUND);
        setFocusable(false);
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int y = MARGIN;
        y = drawLabel(g2d, "SCORE", String.valueOf(gameState.getScore()), y);
        y = drawLabel(g2d, "LEVEL", String.valueOf(gameState.getLevel()), y);
        y = drawLabel(g2d, "LINES", String.valueOf(gameState.getTotalLinesCleared()), y);

        y += MARGIN;
        y = drawPiecePreview(g2d, "HOLD", controller.getHeldType(), controller.isHoldAvailable(), y);

        y += MARGIN;
        drawPiecePreview(g2d, "NEXT", controller.getNextTetrominoType(), true, y);
    }

    private int drawLabel(Graphics2D g2d, String label, String value, int y) {
        g2d.setFont(ColorTheme.FONT_HEADING);
        g2d.setColor(ColorTheme.TEXT_ACCENT);
        g2d.drawString(label, MARGIN, y);

        g2d.setFont(ColorTheme.FONT_BODY);
        g2d.setColor(ColorTheme.TEXT_PRIMARY);
        g2d.drawString(value, MARGIN, y + 24);

        return y + 60;
    }

    private int drawPiecePreview(Graphics2D g2d, String label, TetrominoType type, boolean enabled, int y) {
        g2d.setFont(ColorTheme.FONT_HEADING);
        g2d.setColor(ColorTheme.TEXT_ACCENT);
        g2d.drawString(label, MARGIN, y);

        int boxTop = y + 15;
        g2d.setColor(ColorTheme.NEXT_BOX_BACKGROUND);
        g2d.fillRect(MARGIN, boxTop, NEXT_BOX_SIZE, NEXT_BOX_SIZE);

        if (type != null) {
            int previewCellSize = Constants.CELL_SIZE - 4;
            int[][] shape = type.getShape(0);
            g2d.setColor(enabled ? type.getColor() : dim(type.getColor()));
            for (int[] cell : shape) {
                int px = MARGIN + cell[0] * previewCellSize + 2;
                int py = boxTop + cell[1] * previewCellSize + 2;
                g2d.fillRect(px, py, previewCellSize - 2, previewCellSize - 2);
            }
        }

        return boxTop + NEXT_BOX_SIZE;
    }

    private Color dim(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 90);
    }
}

package com.tetris.view;

import com.tetris.constants.Constants;
import com.tetris.controller.GameController;
import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.Point;
import com.tetris.model.Tetromino;
import com.tetris.model.TetrominoType;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

public final class BoardPanel extends JPanel {

    private final Board board;
    private final GameController controller;
    private final GameState gameState;

    public BoardPanel(Board board, GameController controller, GameState gameState) {
        this.board = board;
        this.controller = controller;
        this.gameState = gameState;
        setPreferredSize(new Dimension(Constants.BOARD_PANEL_WIDTH, Constants.BOARD_PANEL_HEIGHT));
        setBackground(Constants.BOARD_BACKGROUND_COLOR);
        setFocusable(false);
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawFixedBlocks(g2d);
        drawCurrentTetromino(g2d);
        drawGridLines(g2d);

        if (gameState.isGameOver()) {
            drawOverlay(g2d, "GAME OVER");
        } else if (controller.isPaused()) {
            drawOverlay(g2d, "PAUSED");
        }
    }

    private void drawFixedBlocks(Graphics2D g2d) {
        TetrominoType[][] grid = board.getGridSnapshot();
        for (int y = Constants.BOARD_HIDDEN_ROWS; y < Constants.BOARD_TOTAL_HEIGHT; y++) {
            for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
                TetrominoType type = grid[y][x];
                if (type != null) {
                    drawCell(g2d, x, y - Constants.BOARD_HIDDEN_ROWS, type.getColor());
                }
            }
        }
    }

    private void drawCurrentTetromino(Graphics2D g2d) {
        Tetromino current = controller.getCurrentTetromino();
        if (current == null) {
            return;
        }
        List<Point> cells = current.getOccupiedCells();
        Color color = current.getType().getColor();
        for (Point cell : cells) {
            int visibleY = cell.getY() - Constants.BOARD_HIDDEN_ROWS;
            if (visibleY >= 0) {
                drawCell(g2d, cell.getX(), visibleY, color);
            }
        }
    }

    private void drawCell(Graphics2D g2d, int col, int row, Color color) {
        int px = col * Constants.CELL_SIZE;
        int py = row * Constants.CELL_SIZE;

        g2d.setColor(color);
        g2d.fillRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);

        g2d.setColor(ColorTheme.BLOCK_BORDER_COLOR);
        g2d.drawRect(px, py, Constants.CELL_SIZE - 1, Constants.CELL_SIZE - 1);
    }

    private void drawGridLines(Graphics2D g2d) {
        g2d.setColor(Constants.GRID_LINE_COLOR);
        for (int x = 0; x <= Constants.BOARD_WIDTH; x++) {
            int px = x * Constants.CELL_SIZE;
            g2d.drawLine(px, 0, px, Constants.BOARD_PANEL_HEIGHT);
        }
        for (int y = 0; y <= Constants.BOARD_HEIGHT; y++) {
            int py = y * Constants.CELL_SIZE;
            g2d.drawLine(0, py, Constants.BOARD_PANEL_WIDTH, py);
        }
    }

    private void drawOverlay(Graphics2D g2d, String message) {
        g2d.setColor(ColorTheme.OVERLAY_COLOR);
        g2d.fillRect(0, 0, Constants.BOARD_PANEL_WIDTH, Constants.BOARD_PANEL_HEIGHT);

        g2d.setColor(ColorTheme.TEXT_PRIMARY);
        g2d.setFont(ColorTheme.FONT_OVERLAY);
        int textWidth = g2d.getFontMetrics().stringWidth(message);
        int x = (Constants.BOARD_PANEL_WIDTH - textWidth) / 2;
        int y = Constants.BOARD_PANEL_HEIGHT / 2;
        g2d.drawString(message, x, y);
    }
}

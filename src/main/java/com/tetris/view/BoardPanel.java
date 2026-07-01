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

/**
 * テトリスのボード盤面（固定済みブロックおよび操作中のテトリミノ）を描画するパネル。
 *
 * <p>このクラスはロジックを一切持たず、{@link Board} と {@link GameController} が
 * 保持する状態を読み取って描画するのみである（MVCにおけるView）。</p>
 */
public final class BoardPanel extends JPanel {

    private final Board board;
    private final GameController controller;
    private final GameState gameState;

    /**
     * ボード描画パネルを生成する。
     *
     * @param board      描画対象のボード
     * @param controller 操作中テトリミノやゲーム状態を参照するためのController
     * @param gameState  ゲームオーバー判定表示のためのゲーム状態
     */
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

    /**
     * ボードに固定済みのブロックを描画する。
     * <p>バッファ領域（画面上部の非表示行）は描画対象から除外する。</p>
     *
     * @param g2d 描画コンテキスト
     */
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

    /**
     * 現在操作中のテトリミノを描画する。
     * <p>バッファ領域内にあるブロックは画面上に描画しない。</p>
     *
     * @param g2d 描画コンテキスト
     */
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

    /**
     * 1マス分のブロックを描画する。
     *
     * @param g2d   描画コンテキスト
     * @param col   描画対象の列（画面上の表示座標）
     * @param row   描画対象の行（画面上の表示座標）
     * @param color 塗りつぶし色
     */
    private void drawCell(Graphics2D g2d, int col, int row, Color color) {
        int px = col * Constants.CELL_SIZE;
        int py = row * Constants.CELL_SIZE;

        g2d.setColor(color);
        g2d.fillRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);

        g2d.setColor(ColorTheme.BLOCK_BORDER_COLOR);
        g2d.drawRect(px, py, Constants.CELL_SIZE - 1, Constants.CELL_SIZE - 1);
    }

    /**
     * ボード全体にグリッド線を描画する。
     *
     * @param g2d 描画コンテキスト
     */
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

    /**
     * ボード全体を覆う半透明のオーバーレイと、中央にメッセージを描画する。
     * <p>一時停止・ゲームオーバー時の表示に使用する。</p>
     *
     * @param g2d     描画コンテキスト
     * @param message 表示するメッセージ
     */
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

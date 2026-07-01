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

/**
 * スコア・レベル・消去ライン数・NEXT（次のテトリミノ）を表示するサイドパネル。
 *
 * <p>Boardの描画とは責務を分離し、このクラスはゲーム進行状況の可視化のみを担う。</p>
 */
public final class SidePanel extends JPanel {

    private static final int NEXT_BOX_SIZE = 4 * Constants.CELL_SIZE;
    private static final int MARGIN = 20;

    private final GameController controller;
    private final GameState gameState;

    /**
     * サイドパネルを生成する。
     *
     * @param controller 次のテトリミノを参照するためのController
     * @param gameState  スコア・レベル等を参照するためのゲーム状態
     */
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

    /**
     * ラベルと値のペアを描画する。
     *
     * @param g2d   描画コンテキスト
     * @param label ラベル文字列（例: "SCORE"）
     * @param value 表示する値
     * @param y     描画開始のy座標
     * @return 次の要素を描画すべきy座標
     */
    private int drawLabel(Graphics2D g2d, String label, String value, int y) {
        g2d.setFont(ColorTheme.FONT_HEADING);
        g2d.setColor(ColorTheme.TEXT_ACCENT);
        g2d.drawString(label, MARGIN, y);

        g2d.setFont(ColorTheme.FONT_BODY);
        g2d.setColor(ColorTheme.TEXT_PRIMARY);
        g2d.drawString(value, MARGIN, y + 24);

        return y + 60;
    }

    /**
     * テトリミノのプレビュー（NEXT または HOLD）を描画する。
     *
     * @param g2d     描画コンテキスト
     * @param label   見出し文字列（"NEXT" または "HOLD"）
     * @param type    表示するテトリミノ種別。何も表示しない場合は {@code null}
     * @param enabled 使用可能かどうか。{@code false} の場合はミノを暗く（半透明に）表示する
     * @param y       描画開始のy座標
     * @return 次の要素を描画すべきy座標
     */
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

    /**
     * 使用不可（ホールド済み）を表現するため、指定色を半透明に変換する。
     *
     * @param color 元の色
     * @return 半透明化した色
     */
    private Color dim(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 90);
    }
}

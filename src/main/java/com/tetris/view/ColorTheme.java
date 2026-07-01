package com.tetris.view;

import java.awt.Color;
import java.awt.Font;

/**
 * UI全般の配色・フォントを定義するテーマクラス。
 *
 * <p>テトリミノ自体の色は {@code TetrominoType} が保持するため、
 * このクラスはそれ以外のUI要素（背景、テキスト、オーバーレイ等）の見た目を集約する。</p>
 */
public final class ColorTheme {

    /**
     * インスタンス化を防止するためのプライベートコンストラクタ。
     */
    private ColorTheme() {
        throw new AssertionError("ColorTheme クラスはインスタンス化できません。");
    }

    /** サイドパネルの背景色。 */
    public static final Color SIDE_PANEL_BACKGROUND = new Color(30, 30, 30);

    /** 通常テキストの色。 */
    public static final Color TEXT_PRIMARY = Color.WHITE;

    /** 見出しテキストの色。 */
    public static final Color TEXT_ACCENT = new Color(0, 240, 240);

    /** 一時停止・ゲームオーバー時のオーバーレイ色（半透明）。 */
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 160);

    /** ブロックの縁取り色。 */
    public static final Color BLOCK_BORDER_COLOR = new Color(255, 255, 255, 90);

    /** NEXT表示枠の背景色。 */
    public static final Color NEXT_BOX_BACKGROUND = new Color(20, 20, 20);

    /** 見出し用フォント。 */
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 20);

    /** 通常テキスト用フォント。 */
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 16);

    /** オーバーレイの大見出し用フォント。 */
    public static final Font FONT_OVERLAY = new Font("SansSerif", Font.BOLD, 32);
}

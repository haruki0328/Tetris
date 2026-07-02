package com.tetris.constants;

import java.awt.Color;

public final class Constants {

    private Constants() {
        throw new AssertionError("Constants クラスはインスタンス化できません。");
    }

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    // 出現直後のミノが見た目の盤面より上にはみ出せるよう、上に2行分バッファを持たせる
    public static final int BOARD_HIDDEN_ROWS = 2;
    public static final int BOARD_TOTAL_HEIGHT = BOARD_HEIGHT + BOARD_HIDDEN_ROWS;

    public static final int CELL_SIZE = 30;
    public static final int SIDE_PANEL_WIDTH = 180;
    public static final int BOARD_PANEL_WIDTH = BOARD_WIDTH * CELL_SIZE;
    public static final int BOARD_PANEL_HEIGHT = BOARD_HEIGHT * CELL_SIZE;
    public static final int WINDOW_WIDTH = BOARD_PANEL_WIDTH + SIDE_PANEL_WIDTH;
    public static final int WINDOW_HEIGHT = BOARD_PANEL_HEIGHT;
    public static final String WINDOW_TITLE = "Tetris (MVC Edition)";

    public static final Color GRID_LINE_COLOR = new Color(60, 60, 60);
    public static final Color BOARD_BACKGROUND_COLOR = Color.BLACK;
    public static final Color EMPTY_CELL_COLOR = null;

    public static final int INITIAL_DROP_INTERVAL_MS = 1000;
    public static final int DROP_INTERVAL_DECREASE_PER_LEVEL_MS = 50;
    public static final int MIN_DROP_INTERVAL_MS = 100;
    public static final int SOFT_DROP_INTERVAL_MS = 50;
    public static final int LINES_PER_LEVEL = 10;

    public static final int SCORE_SINGLE_LINE = 100;
    public static final int SCORE_DOUBLE_LINE = 300;
    public static final int SCORE_TRIPLE_LINE = 500;
    public static final int SCORE_TETRIS = 800;
    public static final int SCORE_SOFT_DROP_PER_CELL = 1;
    public static final int SCORE_HARD_DROP_PER_CELL = 2;

    // index 0は未使用（0ライン消去はスコアなし）
    public static final int[] LINE_CLEAR_SCORES = {
            0,
            SCORE_SINGLE_LINE,
            SCORE_DOUBLE_LINE,
            SCORE_TRIPLE_LINE,
            SCORE_TETRIS
    };

    public static final int ROTATION_STATES = 4;
    public static final int BLOCKS_PER_TETROMINO = 4;
    public static final int SPAWN_COLUMN_OFFSET = (BOARD_WIDTH - 4) / 2;
    public static final int SPAWN_ROW_OFFSET = 0;

    public static final int DAS_DELAY_MS = 170;
    public static final int DAS_REPEAT_INTERVAL_MS = 50;
}

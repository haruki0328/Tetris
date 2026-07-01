package com.tetris.constants;

import java.awt.Color;

public final class Constants {

    private Constants() {
        throw new AssertionError("Constants クラスはインスタンス化できません。");
    }

    // ==============================
    // ボードサイズ関連
    // ==============================

    /** ボードの幅（セル数）。 */
    public static final int BOARD_WIDTH = 10;

    /** ボードの高さ（セル数）。 */
    public static final int BOARD_HEIGHT = 20;

    /**
     * ボード上部の非表示バッファ行数。
     * <p>新規テトリミノがボード上端より上に出現できるよう、
     * 見た目上の高さより余分に行を確保する。</p>
     */
    public static final int BOARD_HIDDEN_ROWS = 2;

    /** バッファ行を含めたボードの内部的な総行数。 */
    public static final int BOARD_TOTAL_HEIGHT = BOARD_HEIGHT + BOARD_HIDDEN_ROWS;

    // ==============================
    // 描画関連
    // ==============================

    /** 1セルあたりの描画サイズ（ピクセル）。 */
    public static final int CELL_SIZE = 30;

    /** サイドパネル（スコア・NEXT表示）の幅（ピクセル）。 */
    public static final int SIDE_PANEL_WIDTH = 180;

    /** ボード描画パネルの幅（ピクセル）。 */
    public static final int BOARD_PANEL_WIDTH = BOARD_WIDTH * CELL_SIZE;

    /** ボード描画パネルの高さ（ピクセル）。 */
    public static final int BOARD_PANEL_HEIGHT = BOARD_HEIGHT * CELL_SIZE;

    /** ウィンドウ全体の幅（ピクセル）。 */
    public static final int WINDOW_WIDTH = BOARD_PANEL_WIDTH + SIDE_PANEL_WIDTH;

    /** ウィンドウ全体の高さ（ピクセル）。 */
    public static final int WINDOW_HEIGHT = BOARD_PANEL_HEIGHT;

    /** ウィンドウタイトル。 */
    public static final String WINDOW_TITLE = "Tetris (MVC Edition)";

    /** グリッド線の色。 */
    public static final Color GRID_LINE_COLOR = new Color(60, 60, 60);

    /** ボードの背景色。 */
    public static final Color BOARD_BACKGROUND_COLOR = Color.BLACK;

    /** 空セルを示す色（未使用マーカー）。 */
    public static final Color EMPTY_CELL_COLOR = null;

    // ==============================
    // ゲームループ・落下速度関連
    // ==============================

    /** ゲーム開始時の落下間隔（ミリ秒）。 */
    public static final int INITIAL_DROP_INTERVAL_MS = 1000;

    /** レベルが1上がるごとに落下間隔が短縮される割合（ミリ秒）。 */
    public static final int DROP_INTERVAL_DECREASE_PER_LEVEL_MS = 50;

    /** 落下間隔の下限（ミリ秒）。これ以上は速くならない。 */
    public static final int MIN_DROP_INTERVAL_MS = 100;

    /** ソフトドロップ（下キー長押し）時の落下間隔（ミリ秒）。 */
    public static final int SOFT_DROP_INTERVAL_MS = 50;

    /** 何ライン消去するごとにレベルが1上昇するか。 */
    public static final int LINES_PER_LEVEL = 10;

    // ==============================
    // スコア関連
    // ==============================

    /** 1ライン消去時の基本スコア。 */
    public static final int SCORE_SINGLE_LINE = 100;

    /** 2ライン同時消去時の基本スコア。 */
    public static final int SCORE_DOUBLE_LINE = 300;

    /** 3ライン同時消去時の基本スコア。 */
    public static final int SCORE_TRIPLE_LINE = 500;

    /** 4ライン同時消去（テトリス）時の基本スコア。 */
    public static final int SCORE_TETRIS = 800;

    /** ソフトドロップ1マスあたりの加算スコア。 */
    public static final int SCORE_SOFT_DROP_PER_CELL = 1;

    /** ハードドロップ1マスあたりの加算スコア。 */
    public static final int SCORE_HARD_DROP_PER_CELL = 2;

    /**
     * 同時消去ライン数に応じたスコア一覧。
     * <p>インデックス0は未使用（0ライン消去はスコア加算なし）、
     * インデックス1〜4がそれぞれ1〜4ライン消去に対応する。</p>
     */
    public static final int[] LINE_CLEAR_SCORES = {
            0,
            SCORE_SINGLE_LINE,
            SCORE_DOUBLE_LINE,
            SCORE_TRIPLE_LINE,
            SCORE_TETRIS
    };

    // ==============================
    // テトリミノ関連
    // ==============================

    /** テトリミノの回転状態数（0°, 90°, 180°, 270°）。 */
    public static final int ROTATION_STATES = 4;

    /** 1つのテトリミノを構成するブロック数。 */
    public static final int BLOCKS_PER_TETROMINO = 4;

    /**
     * テトリミノ出現時の基準スポーン列（左上基準のX座標オフセット）。
     * <p>4x4のバウンディングボックス基準でボード中央付近に出現させる。</p>
     */
    public static final int SPAWN_COLUMN_OFFSET = (BOARD_WIDTH - 4) / 2;

    /**
     * テトリミノ出現時の基準スポーン行（バッファ領域内のY座標オフセット）。
     */
    public static final int SPAWN_ROW_OFFSET = 0;

    // ==============================
    // キー入力関連（キーリピート制御）
    // ==============================

    /** 左右移動キーの初回リピートまでの遅延（ミリ秒）。 */
    public static final int DAS_DELAY_MS = 170;

    /** 左右移動キーのリピート間隔（ミリ秒）。 */
    public static final int DAS_REPEAT_INTERVAL_MS = 50;
}

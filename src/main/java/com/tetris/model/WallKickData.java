package com.tetris.model;

/**
 * テトリミノ回転時のWall Kick（壁蹴り）に使用するオフセット候補を提供するクラス。
 *
 * <p>正規のSRS（Super Rotation System）は回転状態の遷移ペアごとに異なる
 * オフセットテーブルを持つが、本実装では簡易版として、回転先の形状が
 * その場で置けない場合に試行する共通オフセット群を1種類だけ用意する。</p>
 *
 * <p>{@code Board} は回転後の形状が現在位置で衝突する場合、このクラスが
 * 提供するオフセットを順番に試し、最初に配置可能となったオフセットを採用する。</p>
 */
public final class WallKickData {

    /**
     * インスタンス化を防止するためのプライベートコンストラクタ。
     */
    private WallKickData() {
        throw new AssertionError("WallKickData クラスはインスタンス化できません。");
    }

    /**
     * 通常のテトリミノ（J, L, S, T, Z）回転時に試行するオフセット候補。
     * <p>各要素は {dx, dy} を表す。先頭の {0, 0} はオフセットなし（通常回転）を意味する。</p>
     */
    private static final int[][] STANDARD_KICKS = {
            {0, 0},
            {-1, 0},
            {1, 0},
            {0, -1},
            {-1, -1},
            {1, -1},
            {0, 1}
    };

    /**
     * I型テトリミノ回転時に試行するオフセット候補。
     * <p>I型は他のミノよりバウンディングボックスが大きいため、
     * より広い範囲のオフセットを候補として用意する。</p>
     */
    private static final int[][] I_PIECE_KICKS = {
            {0, 0},
            {-1, 0},
            {1, 0},
            {-2, 0},
            {2, 0},
            {0, -1},
            {0, 1}
    };

    /**
     * 指定したテトリミノ種別に応じたWall Kickオフセット候補を取得する。
     *
     * <p>O型は回転しても形状が変化しないため、オフセットなしの1件のみを返す。</p>
     *
     * @param type 対象のテトリミノ種別
     * @return 試行すべきオフセット候補（{dx, dy} の配列）。先頭ほど優先度が高い。
     */
    public static int[][] getKicksFor(TetrominoType type) {
        if (type == TetrominoType.O) {
            return new int[][]{{0, 0}};
        }
        if (type == TetrominoType.I) {
            return I_PIECE_KICKS;
        }
        return STANDARD_KICKS;
    }
}

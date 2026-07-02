package com.tetris.model;

// 正式なSRSではなく簡易版のWall Kick実装。回転先がその場で置けない場合に
// 試すオフセット候補を種別ごとに1パターンだけ用意している。
public final class WallKickData {

    private WallKickData() {
        throw new AssertionError("WallKickData クラスはインスタンス化できません。");
    }

    private static final int[][] STANDARD_KICKS = {
            {0, 0},
            {-1, 0},
            {1, 0},
            {0, -1},
            {-1, -1},
            {1, -1},
            {0, 1}
    };

    private static final int[][] I_PIECE_KICKS = {
            {0, 0},
            {-1, 0},
            {1, 0},
            {-2, 0},
            {2, 0},
            {0, -1},
            {0, 1}
    };

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

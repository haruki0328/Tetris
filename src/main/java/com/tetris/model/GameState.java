package com.tetris.model;

import com.tetris.constants.Constants;

/**
 * スコア、レベル、累計消去ライン数、ゲームオーバー状態を管理するクラス。
 *
 * <p>{@code Board} が行削除やドロップ操作を検知した際にこのクラスへ通知し、
 * スコア加算・レベルアップの判定を行わせる設計とする。</p>
 */
public final class GameState {

    private int score;
    private int level;
    private int totalLinesCleared;
    private boolean gameOver;

    /**
     * 初期状態（スコア0、レベル1、非ゲームオーバー）でゲーム状態を生成する。
     */
    public GameState() {
        this.score = 0;
        this.level = 1;
        this.totalLinesCleared = 0;
        this.gameOver = false;
    }

    /**
     * 現在のスコアを取得する。
     *
     * @return 現在のスコア
     */
    public int getScore() {
        return score;
    }

    /**
     * 現在のレベルを取得する。
     *
     * @return 現在のレベル（1始まり）
     */
    public int getLevel() {
        return level;
    }

    /**
     * これまでに消去した累計ライン数を取得する。
     *
     * @return 累計消去ライン数
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    /**
     * ゲームオーバー状態かどうかを取得する。
     *
     * @return ゲームオーバーであれば {@code true}
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * ゲームオーバー状態に設定する。
     */
    public void setGameOver() {
        this.gameOver = true;
    }

    /**
     * ライン消去に応じてスコアとレベルを更新する。
     *
     * <p>同時消去ライン数に応じたスコアに現在のレベルを乗算して加算し、
     * 累計消去ライン数が閾値を超えた場合はレベルアップを行う。</p>
     *
     * @param linesClearedAtOnce 同時に消去されたライン数（1〜4）
     */
    public void applyLineClear(int linesClearedAtOnce) {
        if (linesClearedAtOnce <= 0 || linesClearedAtOnce >= Constants.LINE_CLEAR_SCORES.length) {
            return;
        }
        int baseScore = Constants.LINE_CLEAR_SCORES[linesClearedAtOnce];
        this.score += baseScore * this.level;
        this.totalLinesCleared += linesClearedAtOnce;
        updateLevel();
    }

    /**
     * ソフトドロップ操作によるスコアを加算する。
     *
     * @param cellsDropped ソフトドロップで移動したセル数
     */
    public void applySoftDropScore(int cellsDropped) {
        this.score += cellsDropped * Constants.SCORE_SOFT_DROP_PER_CELL;
    }

    /**
     * ハードドロップ操作によるスコアを加算する。
     *
     * @param cellsDropped ハードドロップで移動したセル数
     */
    public void applyHardDropScore(int cellsDropped) {
        this.score += cellsDropped * Constants.SCORE_HARD_DROP_PER_CELL;
    }

    /**
     * 累計消去ライン数に基づきレベルを再計算する。
     */
    private void updateLevel() {
        this.level = (totalLinesCleared / Constants.LINES_PER_LEVEL) + 1;
    }

    /**
     * 現在のレベルに応じた落下間隔（ミリ秒）を計算する。
     *
     * @return 落下間隔（ミリ秒）。下限値 {@link Constants#MIN_DROP_INTERVAL_MS} を下回らない。
     */
    public int getCurrentDropIntervalMs() {
        int interval = Constants.INITIAL_DROP_INTERVAL_MS
                - (level - 1) * Constants.DROP_INTERVAL_DECREASE_PER_LEVEL_MS;
        return Math.max(interval, Constants.MIN_DROP_INTERVAL_MS);
    }
}

package com.tetris.model;

import com.tetris.constants.Constants;

public final class GameState {

    private int score;
    private int level;
    private int totalLinesCleared;
    private boolean gameOver;

    public GameState() {
        this.score = 0;
        this.level = 1;
        this.totalLinesCleared = 0;
        this.gameOver = false;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver() {
        this.gameOver = true;
    }

    public void applyLineClear(int linesClearedAtOnce) {
        if (linesClearedAtOnce <= 0 || linesClearedAtOnce >= Constants.LINE_CLEAR_SCORES.length) {
            return;
        }
        int baseScore = Constants.LINE_CLEAR_SCORES[linesClearedAtOnce];
        this.score += baseScore * this.level;
        this.totalLinesCleared += linesClearedAtOnce;
        updateLevel();
    }

    public void applySoftDropScore(int cellsDropped) {
        this.score += cellsDropped * Constants.SCORE_SOFT_DROP_PER_CELL;
    }

    public void applyHardDropScore(int cellsDropped) {
        this.score += cellsDropped * Constants.SCORE_HARD_DROP_PER_CELL;
    }

    private void updateLevel() {
        this.level = (totalLinesCleared / Constants.LINES_PER_LEVEL) + 1;
    }

    public int getCurrentDropIntervalMs() {
        int interval = Constants.INITIAL_DROP_INTERVAL_MS
                - (level - 1) * Constants.DROP_INTERVAL_DECREASE_PER_LEVEL_MS;
        return Math.max(interval, Constants.MIN_DROP_INTERVAL_MS);
    }
}

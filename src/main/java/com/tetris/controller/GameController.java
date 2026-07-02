package com.tetris.controller;

import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.Tetromino;
import com.tetris.model.TetrominoFactory;
import com.tetris.model.TetrominoType;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public final class GameController {

    private final Board board;
    private final GameState gameState;
    private final TetrominoFactory tetrominoFactory;
    private final List<ViewRefreshListener> refreshListeners;

    private Tetromino currentTetromino;
    private TetrominoType nextType;
    private TetrominoType heldType;
    private boolean holdAvailable;

    private Timer dropTimer;
    private boolean softDropping;
    private boolean running;
    private boolean paused;

    public GameController(Board board, GameState gameState) {
        this.board = board;
        this.gameState = gameState;
        this.tetrominoFactory = new TetrominoFactory();
        this.refreshListeners = new ArrayList<>();
        this.softDropping = false;
        this.running = false;
        this.heldType = null;
        this.holdAvailable = true;

        this.nextType = tetrominoFactory.next();
        spawnNextTetromino();
    }

    public void addRefreshListener(ViewRefreshListener listener) {
        this.refreshListeners.add(listener);
    }

    private void notifyRefresh() {
        for (ViewRefreshListener listener : refreshListeners) {
            listener.onRefresh();
        }
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        dropTimer = new Timer(gameState.getCurrentDropIntervalMs(), e -> onDropTick());
        dropTimer.start();
    }

    public void stop() {
        running = false;
        if (dropTimer != null) {
            dropTimer.stop();
        }
    }

    private void onDropTick() {
        if (gameState.isGameOver()) {
            stop();
            return;
        }
        if (paused) {
            return;
        }

        Tetromino moved = board.tryMove(currentTetromino, 0, 1);
        if (moved != null) {
            currentTetromino = moved;
        } else {
            lockAndSpawnNext();
        }
        notifyRefresh();
    }

    private void lockAndSpawnNext() {
        int clearedLines = board.lockTetromino(currentTetromino);
        if (clearedLines > 0) {
            gameState.applyLineClear(clearedLines);
            applyCurrentLevelToTimer();
        }
        spawnNextTetromino();
        holdAvailable = true;

        if (board.isGameOverAt(currentTetromino)) {
            gameState.setGameOver();
            stop();
        }
    }

    private void spawnNextTetromino() {
        this.currentTetromino = Tetromino.spawn(nextType);
        this.nextType = tetrominoFactory.next();
    }

    private void applyCurrentLevelToTimer() {
        if (dropTimer != null && !softDropping) {
            dropTimer.setDelay(gameState.getCurrentDropIntervalMs());
        }
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public TetrominoType getNextTetrominoType() {
        return nextType;
    }

    public TetrominoType getHeldType() {
        return heldType;
    }

    public boolean isHoldAvailable() {
        return holdAvailable;
    }

    public void moveLeft() {
        applyMoveIfPossible(-1, 0);
    }

    public void moveRight() {
        applyMoveIfPossible(1, 0);
    }

    private void applyMoveIfPossible(int dx, int dy) {
        if (gameState.isGameOver() || paused) {
            return;
        }
        Tetromino moved = board.tryMove(currentTetromino, dx, dy);
        if (moved != null) {
            currentTetromino = moved;
            notifyRefresh();
        }
    }

    public void rotateClockwise() {
        applyRotationIfPossible(1);
    }

    public void rotateCounterClockwise() {
        applyRotationIfPossible(-1);
    }

    private void applyRotationIfPossible(int direction) {
        if (gameState.isGameOver() || paused) {
            return;
        }
        Tetromino rotated = board.tryRotate(currentTetromino, direction);
        if (rotated != null) {
            currentTetromino = rotated;
            notifyRefresh();
        }
    }

    public void startSoftDrop() {
        if (softDropping || dropTimer == null || paused) {
            return;
        }
        softDropping = true;
        dropTimer.setDelay(com.tetris.constants.Constants.SOFT_DROP_INTERVAL_MS);
    }

    public void stopSoftDrop() {
        if (!softDropping || dropTimer == null) {
            return;
        }
        softDropping = false;
        dropTimer.setDelay(gameState.getCurrentDropIntervalMs());
    }

    public void softDropStep() {
        if (gameState.isGameOver()) {
            return;
        }
        Tetromino moved = board.tryMove(currentTetromino, 0, 1);
        if (moved != null) {
            currentTetromino = moved;
            gameState.applySoftDropScore(1);
            notifyRefresh();
        }
    }

    public void hardDrop() {
        if (gameState.isGameOver() || paused) {
            return;
        }
        Tetromino dropped = board.calculateDropPosition(currentTetromino);
        int cellsDropped = dropped.getPosition().getY() - currentTetromino.getPosition().getY();
        gameState.applyHardDropScore(cellsDropped);
        currentTetromino = dropped;

        lockAndSpawnNext();
        notifyRefresh();
    }

    // ホールド枠が空なら現在のミノを預けて次を出現させ、既にあれば入れ替える。
    // 1つ固定されるまでは再度ホールドできないようにして無限入れ替えを防ぐ。
    public void hold() {
        if (gameState.isGameOver() || paused || !holdAvailable) {
            return;
        }

        TetrominoType currentType = currentTetromino.getType();

        if (heldType == null) {
            heldType = currentType;
            spawnNextTetromino();
        } else {
            TetrominoType swappedOutType = heldType;
            heldType = currentType;
            currentTetromino = Tetromino.spawn(swappedOutType);
        }

        holdAvailable = false;

        if (board.isGameOverAt(currentTetromino)) {
            gameState.setGameOver();
            stop();
        }

        notifyRefresh();
    }

    public void togglePause() {
        if (gameState.isGameOver() || !running) {
            return;
        }
        paused = !paused;
        if (paused) {
            dropTimer.stop();
        } else {
            dropTimer.start();
        }
        notifyRefresh();
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }
}

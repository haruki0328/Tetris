package com.tetris.model;

import com.tetris.constants.Constants;
import com.tetris.event.GameEvent;
import com.tetris.event.GameEventListener;

import java.util.ArrayList;
import java.util.List;

// 盤面の状態管理と衝突判定を担当するクラス。View/Controllerの実装は知らず、
// GameEventListener経由でしか外部に通知しない。
public final class Board {

    private final TetrominoType[][] grid;
    private final List<GameEventListener> listeners;

    public Board() {
        this.grid = new TetrominoType[Constants.BOARD_TOTAL_HEIGHT][Constants.BOARD_WIDTH];
        this.listeners = new ArrayList<>();
    }

    public void addListener(GameEventListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }

    private boolean isCellFreeAndInBounds(int x, int y) {
        if (x < 0 || x >= Constants.BOARD_WIDTH) {
            return false;
        }
        if (y < 0 || y >= Constants.BOARD_TOTAL_HEIGHT) {
            return false;
        }
        return grid[y][x] == null;
    }

    public boolean isValidPosition(Tetromino tetromino) {
        for (Point cell : tetromino.getOccupiedCells()) {
            if (!isCellFreeAndInBounds(cell.getX(), cell.getY())) {
                return false;
            }
        }
        return true;
    }

    public Tetromino tryMove(Tetromino tetromino, int dx, int dy) {
        Tetromino moved = tetromino.moved(dx, dy);
        return isValidPosition(moved) ? moved : null;
    }

    // 回転先がそのまま置けない場合、WallKickDataのオフセット候補を順番に試す
    public Tetromino tryRotate(Tetromino tetromino, int direction) {
        int newRotationState = tetromino.getRotationState() + direction;
        int[][] kicks = WallKickData.getKicksFor(tetromino.getType());

        for (int[] kick : kicks) {
            Tetromino candidate = tetromino.rotatedWithOffset(newRotationState, kick[0], kick[1]);
            if (isValidPosition(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    public int lockTetromino(Tetromino tetromino) {
        for (Point cell : tetromino.getOccupiedCells()) {
            if (cell.getY() >= 0 && cell.getY() < Constants.BOARD_TOTAL_HEIGHT
                    && cell.getX() >= 0 && cell.getX() < Constants.BOARD_WIDTH) {
                grid[cell.getY()][cell.getX()] = tetromino.getType();
            }
        }
        notifyListeners(GameEvent.PIECE_LOCKED);

        int clearedLines = clearFullLines();
        if (clearedLines > 0) {
            notifyListeners(GameEvent.LINES_CLEARED);
        }
        return clearedLines;
    }

    public Tetromino calculateDropPosition(Tetromino tetromino) {
        Tetromino current = tetromino;
        Tetromino next = tryMove(current, 0, 1);
        while (next != null) {
            current = next;
            next = tryMove(current, 0, 1);
        }
        return current;
    }

    private int clearFullLines() {
        List<Integer> fullRowIndexes = new ArrayList<>();
        for (int y = 0; y < Constants.BOARD_TOTAL_HEIGHT; y++) {
            if (isRowFull(y)) {
                fullRowIndexes.add(y);
            }
        }

        for (int row : fullRowIndexes) {
            removeRowAndShiftDown(row);
        }

        return fullRowIndexes.size();
    }

    private boolean isRowFull(int y) {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            if (grid[y][x] == null) {
                return false;
            }
        }
        return true;
    }

    private void removeRowAndShiftDown(int rowToRemove) {
        for (int y = rowToRemove; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, Constants.BOARD_WIDTH);
        }
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            grid[0][x] = null;
        }
    }

    public boolean isGameOverAt(Tetromino spawned) {
        boolean blocked = !isValidPosition(spawned);
        if (blocked) {
            notifyListeners(GameEvent.GAME_OVER);
        }
        return blocked;
    }

    public TetrominoType[][] getGridSnapshot() {
        TetrominoType[][] copy = new TetrominoType[Constants.BOARD_TOTAL_HEIGHT][Constants.BOARD_WIDTH];
        for (int y = 0; y < Constants.BOARD_TOTAL_HEIGHT; y++) {
            System.arraycopy(grid[y], 0, copy[y], 0, Constants.BOARD_WIDTH);
        }
        return copy;
    }
}

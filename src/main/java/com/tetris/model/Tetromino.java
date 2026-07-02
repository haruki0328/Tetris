package com.tetris.model;

import com.tetris.constants.Constants;

import java.util.ArrayList;
import java.util.List;

// 1つのテトリミノ（種別・回転状態・位置）を表す不変クラス。
// 移動・回転は新しいインスタンスを返すだけで、実際に反映するかは
// Board側の衝突判定を見てController側が判断する。
public final class Tetromino {

    private final TetrominoType type;
    private final int rotationState;
    private final Point position;

    public Tetromino(TetrominoType type, int rotationState, Point position) {
        this.type = type;
        this.rotationState = rotationState % Constants.ROTATION_STATES;
        this.position = position;
    }

    public static Tetromino spawn(TetrominoType type) {
        Point spawnPosition = new Point(Constants.SPAWN_COLUMN_OFFSET, Constants.SPAWN_ROW_OFFSET);
        return new Tetromino(type, 0, spawnPosition);
    }

    public TetrominoType getType() {
        return type;
    }

    public int getRotationState() {
        return rotationState;
    }

    public Point getPosition() {
        return position;
    }

    public List<Point> getOccupiedCells() {
        int[][] relativeShape = type.getShape(rotationState);
        List<Point> cells = new ArrayList<>(Constants.BLOCKS_PER_TETROMINO);
        for (int[] relative : relativeShape) {
            int absoluteX = position.getX() + relative[0];
            int absoluteY = position.getY() + relative[1];
            cells.add(new Point(absoluteX, absoluteY));
        }
        return cells;
    }

    public Tetromino moved(int dx, int dy) {
        return new Tetromino(type, rotationState, position.translate(dx, dy));
    }

    public Tetromino rotatedTo(int newRotationState) {
        int normalized = ((newRotationState % Constants.ROTATION_STATES) + Constants.ROTATION_STATES)
                % Constants.ROTATION_STATES;
        return new Tetromino(type, normalized, position);
    }

    public Tetromino rotatedWithOffset(int newRotationState, int offsetX, int offsetY) {
        return rotatedTo(newRotationState).moved(offsetX, offsetY);
    }
}

package com.tetris.model;

import com.tetris.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * ゲーム上に存在する1つのテトリミノ（種別・回転状態・位置）を表す不変(immutable)クラス。
 *
 * <p>このクラスは自身の状態を変更するメソッドを持たない。移動・回転操作は
 * 新しいインスタンスを生成して返す設計とし、呼び出し側（{@code Board} や
 * {@code GameController}）が衝突判定を行った上で参照を差し替える運用を想定する。</p>
 *
 * <p>位置 {@code position} は、4x4バウンディングボックスの左上がボード上の
 * どの座標にあるかを示す。</p>
 */
public final class Tetromino {

    private final TetrominoType type;
    private final int rotationState;
    private final Point position;

    /**
     * テトリミノを生成する。
     *
     * @param type          テトリミノ種別
     * @param rotationState 回転状態（0〜3）
     * @param position      バウンディングボックス左上のボード座標
     */
    public Tetromino(TetrominoType type, int rotationState, Point position) {
        this.type = type;
        this.rotationState = rotationState % Constants.ROTATION_STATES;
        this.position = position;
    }

    /**
     * 指定したテトリミノ種別について、ボード上部の初期出現位置に配置された
     * 新規テトリミノを生成する。
     *
     * @param type 出現させるテトリミノ種別
     * @return 出現位置に配置されたテトリミノ
     */
    public static Tetromino spawn(TetrominoType type) {
        Point spawnPosition = new Point(Constants.SPAWN_COLUMN_OFFSET, Constants.SPAWN_ROW_OFFSET);
        return new Tetromino(type, 0, spawnPosition);
    }

    /**
     * テトリミノ種別を取得する。
     *
     * @return テトリミノ種別
     */
    public TetrominoType getType() {
        return type;
    }

    /**
     * 現在の回転状態を取得する。
     *
     * @return 回転状態（0〜3）
     */
    public int getRotationState() {
        return rotationState;
    }

    /**
     * バウンディングボックス左上のボード座標を取得する。
     *
     * @return 左上座標
     */
    public Point getPosition() {
        return position;
    }

    /**
     * 現在の種別・回転状態・位置に基づく、ボード上の絶対座標を占める4ブロック分の
     * 座標リストを取得する。
     *
     * @return このテトリミノが占めるボード上の絶対座標のリスト
     */
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

    /**
     * このテトリミノを指定量だけ平行移動した新しいインスタンスを返す。
     *
     * @param dx x方向の移動量
     * @param dy y方向の移動量
     * @return 移動後の新しい {@code Tetromino}
     */
    public Tetromino moved(int dx, int dy) {
        return new Tetromino(type, rotationState, position.translate(dx, dy));
    }

    /**
     * このテトリミノを指定した回転状態に変更した新しいインスタンスを返す。
     * <p>位置はWall Kick適用前の基準位置のまま変更しない。</p>
     *
     * @param newRotationState 新しい回転状態（0〜3。範囲外は自動的に正規化される）
     * @return 回転後の新しい {@code Tetromino}
     */
    public Tetromino rotatedTo(int newRotationState) {
        int normalized = ((newRotationState % Constants.ROTATION_STATES) + Constants.ROTATION_STATES)
                % Constants.ROTATION_STATES;
        return new Tetromino(type, normalized, position);
    }

    /**
     * このテトリミノを指定量だけオフセットした、指定回転状態の新しいインスタンスを返す。
     * <p>主にWall Kick適用時、回転とオフセット移動を同時に行うために使用する。</p>
     *
     * @param newRotationState 新しい回転状態
     * @param offsetX          Wall Kickによるx方向のオフセット
     * @param offsetY          Wall Kickによるy方向のオフセット
     * @return 回転・移動後の新しい {@code Tetromino}
     */
    public Tetromino rotatedWithOffset(int newRotationState, int offsetX, int offsetY) {
        return rotatedTo(newRotationState).moved(offsetX, offsetY);
    }
}

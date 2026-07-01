package com.tetris.model;

import com.tetris.constants.Constants;
import com.tetris.event.GameEvent;
import com.tetris.event.GameEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * テトリスのボード状態を管理するModelの中核クラス。
 *
 * <p>責務は以下の通り。</p>
 * <ul>
 *   <li>固定済みブロックのグリッド状態の保持</li>
 *   <li>テトリミノの移動・回転に対する衝突判定（壁・床・他ブロック）</li>
 *   <li>Wall Kickを考慮した回転試行</li>
 *   <li>そろった行の削除処理</li>
 *   <li>ゲームオーバー判定</li>
 * </ul>
 *
 * <p>View・Controllerへの通知は {@link GameEventListener} を通じて行い、
 * Board自身はViewやControllerの実装を一切参照しない。</p>
 */
public final class Board {

    /**
     * 固定済みブロックのグリッド。
     * <p>{@code grid[y][x]} が {@code null} であれば空セル、
     * それ以外であれば固定されたブロックの種別を表す。</p>
     */
    private final TetrominoType[][] grid;

    private final List<GameEventListener> listeners;

    /**
     * 指定サイズのグリッドを空の状態で初期化する。
     */
    public Board() {
        this.grid = new TetrominoType[Constants.BOARD_TOTAL_HEIGHT][Constants.BOARD_WIDTH];
        this.listeners = new ArrayList<>();
    }

    /**
     * ゲームイベントリスナーを登録する。
     *
     * @param listener 登録するリスナー
     */
    public void addListener(GameEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * 登録済みの全リスナーへイベントを通知する。
     *
     * @param event 通知するイベント種別
     */
    private void notifyListeners(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }

    /**
     * 指定した位置のセルが盤面内であり、かつ空いているかどうかを判定する。
     *
     * @param x 判定する列
     * @param y 判定する行
     * @return 盤面内かつ空セルであれば {@code true}
     */
    private boolean isCellFreeAndInBounds(int x, int y) {
        if (x < 0 || x >= Constants.BOARD_WIDTH) {
            return false;
        }
        if (y < 0 || y >= Constants.BOARD_TOTAL_HEIGHT) {
            return false;
        }
        return grid[y][x] == null;
    }

    /**
     * 指定したテトリミノが、現在の位置・回転状態のままボードに配置可能かどうかを判定する。
     *
     * <p>壁・床との衝突、および既に固定されている他のブロックとの重なりをチェックする。</p>
     *
     * @param tetromino 判定対象のテトリミノ
     * @return 配置可能であれば {@code true}
     */
    public boolean isValidPosition(Tetromino tetromino) {
        for (Point cell : tetromino.getOccupiedCells()) {
            if (!isCellFreeAndInBounds(cell.getX(), cell.getY())) {
                return false;
            }
        }
        return true;
    }

    /**
     * テトリミノの水平・垂直移動を試みる。
     *
     * @param tetromino 移動元のテトリミノ
     * @param dx        x方向の移動量
     * @param dy        y方向の移動量
     * @return 移動後の新しいテトリミノ。移動先が無効な場合は {@code null}
     */
    public Tetromino tryMove(Tetromino tetromino, int dx, int dy) {
        Tetromino moved = tetromino.moved(dx, dy);
        return isValidPosition(moved) ? moved : null;
    }

    /**
     * テトリミノの回転を試みる。回転先がそのまま配置できない場合は、
     * {@link WallKickData} が提供するオフセット候補を順に試行する（Wall Kick）。
     *
     * @param tetromino 回転元のテトリミノ
     * @param direction 回転方向（{@code +1} で時計回り、{@code -1} で反時計回り）
     * @return 回転（またはWall Kick）後の新しいテトリミノ。すべての候補が無効な場合は {@code null}
     */
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

    /**
     * テトリミノを現在の位置でボードに固定する。
     *
     * <p>固定後、そろった行があれば削除し、対応するイベントを通知する。</p>
     *
     * @param tetromino 固定するテトリミノ
     * @return 固定によって削除されたライン数（0であれば削除なし）
     */
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

    /**
     * テトリミノを、現在の列位置のまま可能な限り下方向へ移動させた位置（着地位置）を計算する。
     *
     * <p>ハードドロップやゴーストピース表示に使用する。</p>
     *
     * @param tetromino 対象のテトリミノ
     * @return 落下可能な最下点に配置されたテトリミノ
     */
    public Tetromino calculateDropPosition(Tetromino tetromino) {
        Tetromino current = tetromino;
        Tetromino next = tryMove(current, 0, 1);
        while (next != null) {
            current = next;
            next = tryMove(current, 0, 1);
        }
        return current;
    }

    /**
     * 全セルが埋まっている行を検出し、削除して上の行を下へ詰める。
     *
     * @return 削除された行数
     */
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

    /**
     * 指定した行のすべてのセルが埋まっているかどうかを判定する。
     *
     * @param y 判定する行
     * @return すべて埋まっていれば {@code true}
     */
    private boolean isRowFull(int y) {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            if (grid[y][x] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指定した行を削除し、それより上にあるすべての行を1行分下へシフトする。
     * <p>最上段（バッファ領域の先頭行）は空行として補充する。</p>
     *
     * @param rowToRemove 削除対象の行
     */
    private void removeRowAndShiftDown(int rowToRemove) {
        for (int y = rowToRemove; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, Constants.BOARD_WIDTH);
        }
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            grid[0][x] = null;
        }
    }

    /**
     * 新規テトリミノの出現位置がすでに他のブロックで塞がれているかどうかを判定する。
     * <p>塞がれている場合はゲームオーバーとみなす。</p>
     *
     * @param spawned 出現直後のテトリミノ
     * @return 出現位置が配置不可能（＝ゲームオーバー）であれば {@code true}
     */
    public boolean isGameOverAt(Tetromino spawned) {
        boolean blocked = !isValidPosition(spawned);
        if (blocked) {
            notifyListeners(GameEvent.GAME_OVER);
        }
        return blocked;
    }

    /**
     * 現在の固定済みグリッド状態の読み取り専用コピーを取得する。
     * <p>Viewはこのメソッドを通じてのみボード状態を参照し、直接グリッドを変更することはできない。</p>
     *
     * @return グリッド状態のコピー（{@code [row][col]} 形式、{@code null} は空セル）
     */
    public TetrominoType[][] getGridSnapshot() {
        TetrominoType[][] copy = new TetrominoType[Constants.BOARD_TOTAL_HEIGHT][Constants.BOARD_WIDTH];
        for (int y = 0; y < Constants.BOARD_TOTAL_HEIGHT; y++) {
            System.arraycopy(grid[y], 0, copy[y], 0, Constants.BOARD_WIDTH);
        }
        return copy;
    }
}

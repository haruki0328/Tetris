package com.tetris.controller;

import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.Tetromino;
import com.tetris.model.TetrominoFactory;
import com.tetris.model.TetrominoType;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

/**
 * ゲームループの制御と、キー入力に対応するゲーム進行ロジックを統括するController。
 *
 * <p>{@code javax.swing.Timer} を用いて一定間隔ごとにテトリミノを自動落下させ、
 * {@link Board} / {@link GameState} を操作する。View（Swing）への通知は
 * {@link ViewRefreshListener} を介して行い、ControllerはViewの具象クラスを知らない。</p>
 */
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

    /**
     * Controllerを初期化し、最初のテトリミノを出現させる。
     *
     * @param board     操作対象のボード
     * @param gameState 操作対象のゲーム状態
     */
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

    /**
     * View更新リスナーを登録する。
     *
     * @param listener 登録するリスナー
     */
    public void addRefreshListener(ViewRefreshListener listener) {
        this.refreshListeners.add(listener);
    }

    /**
     * 登録済みの全リスナーへ再描画を通知する。
     */
    private void notifyRefresh() {
        for (ViewRefreshListener listener : refreshListeners) {
            listener.onRefresh();
        }
    }

    /**
     * ゲームループ（自動落下タイマー）を開始する。
     * <p>既に開始済みの場合は何もしない。</p>
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;
        dropTimer = new Timer(gameState.getCurrentDropIntervalMs(), e -> onDropTick());
        dropTimer.start();
    }

    /**
     * ゲームループを停止する。
     */
    public void stop() {
        running = false;
        if (dropTimer != null) {
            dropTimer.stop();
        }
    }

    /**
     * 自動落下タイマーが発火するたびに呼び出される処理。
     * <p>1マス下に移動できればそのまま移動し、移動できなければ
     * テトリミノを固定して次のテトリミノを出現させる。</p>
     */
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

    /**
     * 現在のテトリミノをボードに固定し、ライン削除・スコア加算を行った上で
     * 次のテトリミノを出現させる。出現不可の場合はゲームオーバーとする。
     */
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

    /**
     * 次に出現させるテトリミノをキューから取り出し、現在のテトリミノとして出現させる。
     */
    private void spawnNextTetromino() {
        this.currentTetromino = Tetromino.spawn(nextType);
        this.nextType = tetrominoFactory.next();
    }

    /**
     * レベルアップに応じて、自動落下タイマーの間隔を現在のレベルに合わせて更新する。
     */
    private void applyCurrentLevelToTimer() {
        if (dropTimer != null && !softDropping) {
            dropTimer.setDelay(gameState.getCurrentDropIntervalMs());
        }
    }

    /**
     * 現在操作中のテトリミノを取得する。
     *
     * @return 現在操作中のテトリミノ
     */
    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    /**
     * 次に出現するテトリミノの種別を取得する（NEXT表示用）。
     *
     * @return 次のテトリミノ種別
     */
    public TetrominoType getNextTetrominoType() {
        return nextType;
    }

    /**
     * 現在ホールドされているテトリミノ種別を取得する（HOLD表示用）。
     * <p>何もホールドされていない場合は {@code null} を返す。</p>
     *
     * @return ホールド中のテトリミノ種別、または {@code null}
     */
    public TetrominoType getHeldType() {
        return heldType;
    }

    /**
     * 現在ホールド操作が可能かどうかを取得する。
     * <p>直前のホールド以降、テトリミノがまだ固定されていない場合は {@code false} になる。</p>
     *
     * @return ホールド可能であれば {@code true}
     */
    public boolean isHoldAvailable() {
        return holdAvailable;
    }

    /**
     * 現在のテトリミノを左へ1マス移動させる。
     */
    public void moveLeft() {
        applyMoveIfPossible(-1, 0);
    }

    /**
     * 現在のテトリミノを右へ1マス移動させる。
     */
    public void moveRight() {
        applyMoveIfPossible(1, 0);
    }

    /**
     * 指定したオフセットへの移動が可能であれば適用し、Viewへ再描画を通知する。
     *
     * @param dx x方向の移動量
     * @param dy y方向の移動量
     */
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

    /**
     * 現在のテトリミノを時計回りに回転させる（Wall Kick適用）。
     */
    public void rotateClockwise() {
        applyRotationIfPossible(1);
    }

    /**
     * 現在のテトリミノを反時計回りに回転させる（Wall Kick適用）。
     */
    public void rotateCounterClockwise() {
        applyRotationIfPossible(-1);
    }

    /**
     * 指定方向への回転が可能であれば適用し、Viewへ再描画を通知する。
     *
     * @param direction 回転方向（{@code +1}: 時計回り, {@code -1}: 反時計回り）
     */
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

    /**
     * ソフトドロップを開始する（落下速度を上昇させる）。
     * <p>キー押下時に呼び出す想定。</p>
     */
    public void startSoftDrop() {
        if (softDropping || dropTimer == null || paused) {
            return;
        }
        softDropping = true;
        dropTimer.setDelay(com.tetris.constants.Constants.SOFT_DROP_INTERVAL_MS);
    }

    /**
     * ソフトドロップを終了し、通常の落下速度へ戻す。
     * <p>キー解放時に呼び出す想定。</p>
     */
    public void stopSoftDrop() {
        if (!softDropping || dropTimer == null) {
            return;
        }
        softDropping = false;
        dropTimer.setDelay(gameState.getCurrentDropIntervalMs());
    }

    /**
     * ソフトドロップ1マス分の移動を即時に1回行う。
     * <p>下キー押下中に連続入力された際に、通常移動＋スコア加算を行うために使用する。</p>
     */
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

    /**
     * ハードドロップを実行する。現在のテトリミノを即座に着地位置まで落下させ、固定する。
     */
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

    /**
     * 現在のテトリミノをホールドする。
     *
     * <p>ホールド枠が空であれば、現在のテトリミノをホールドに預けて次のテトリミノを出現させる。
     * 既にホールド中のテトリミノがある場合は、現在のテトリミノと入れ替える。</p>
     *
     * <p>連続ホールドによる無限ループを防ぐため、1つのテトリミノが固定されるまでは
     * 再度ホールドを行うことはできない。</p>
     */
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

    /**
     * ゲームの一時停止状態を切り替える。
     * <p>一時停止中は自動落下タイマーを停止し、移動・回転・ドロップ操作を無効化する。</p>
     */
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

    /**
     * ゲームが一時停止中かどうかを取得する。
     *
     * @return 一時停止中であれば {@code true}
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * ゲームが現在実行中かどうかを取得する。
     *
     * @return 実行中であれば {@code true}
     */
    public boolean isRunning() {
        return running;
    }
}

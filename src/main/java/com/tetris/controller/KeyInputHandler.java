package com.tetris.controller;

import com.tetris.constants.Constants;

import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * キーボード入力を受け付け、{@link GameController} の操作メソッドへ変換するハンドラ。
 *
 * <p>左右移動キーについては、押しっぱなしにした際に一定時間後から高速リピート移動を行う
 * DAS（Delayed Auto Shift）を実装する。回転・ハードドロップは押下の都度1回のみ反応し、
 * ソフトドロップは押下中のみ有効になる。</p>
 *
 * <p>キー割り当て:</p>
 * <ul>
 *   <li>左矢印 / A: 左移動</li>
 *   <li>右矢印 / D: 右移動</li>
 *   <li>下矢印 / S: ソフトドロップ</li>
 *   <li>上矢印 / W: 時計回り回転</li>
 *   <li>Z: 反時計回り回転</li>
 *   <li>スペース: ハードドロップ</li>
 *   <li>P: 一時停止 / 再開</li>
 *   <li>C / Shift: ホールド</li>
 * </ul>
 */
public final class KeyInputHandler extends KeyAdapter {

    private final GameController controller;

    private Timer dasTimer;
    private int dasDirection;

    /**
     * 指定したControllerに対する操作を行うキー入力ハンドラを生成する。
     *
     * @param controller 操作対象のGameController
     */
    public KeyInputHandler(GameController controller) {
        this.controller = controller;
        this.dasDirection = 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                handleDirectionalKeyPressed(-1);
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                handleDirectionalKeyPressed(1);
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                controller.startSoftDrop();
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                controller.rotateClockwise();
                break;

            case KeyEvent.VK_Z:
                controller.rotateCounterClockwise();
                break;

            case KeyEvent.VK_SPACE:
                controller.hardDrop();
                break;

            case KeyEvent.VK_P:
                controller.togglePause();
                break;

            case KeyEvent.VK_C:
            case KeyEvent.VK_SHIFT:
                controller.hold();
                break;

            default:
                // 割り当てのないキーは無視する
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (dasDirection == -1) {
                    stopDas();
                }
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (dasDirection == 1) {
                    stopDas();
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                controller.stopSoftDrop();
                break;

            default:
                // 割り当てのないキーは無視する
                break;
        }
    }

    /**
     * 左右移動キーが押された際の処理。即座に1マス移動させた上で、
     * 一定時間押し続けた場合に備えてDASタイマーを起動する。
     *
     * @param direction 移動方向（{@code -1}: 左, {@code 1}: 右）
     */
    private void handleDirectionalKeyPressed(int direction) {
        if (dasDirection == direction) {
            // 既に同方向のキーリピート処理中であれば何もしない（OSのキーリピートイベント対策）
            return;
        }

        stopDas();
        dasDirection = direction;
        moveByDirection(direction);

        dasTimer = new Timer(Constants.DAS_DELAY_MS, e -> startAutoRepeat(direction));
        dasTimer.setRepeats(false);
        dasTimer.start();
    }

    /**
     * DASの初回遅延が経過した後、高速リピート移動を開始する。
     *
     * @param direction 移動方向
     */
    private void startAutoRepeat(int direction) {
        dasTimer = new Timer(Constants.DAS_REPEAT_INTERVAL_MS, e -> moveByDirection(direction));
        dasTimer.start();
    }

    /**
     * 指定方向にテトリミノを1マス移動させる。
     *
     * @param direction 移動方向（{@code -1}: 左, {@code 1}: 右）
     */
    private void moveByDirection(int direction) {
        if (direction < 0) {
            controller.moveLeft();
        } else {
            controller.moveRight();
        }
    }

    /**
     * DASタイマーを停止し、リピート状態を解除する。
     */
    private void stopDas() {
        if (dasTimer != null) {
            dasTimer.stop();
            dasTimer = null;
        }
        dasDirection = 0;
    }
}

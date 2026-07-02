package com.tetris.controller;

import com.tetris.constants.Constants;

import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// 左右移動キーはDAS（Delayed Auto Shift）で、押しっぱなしにすると
// 一定時間後から高速リピート移動する。回転・ハードドロップは押下ごとに1回のみ反応する。
public final class KeyInputHandler extends KeyAdapter {

    private final GameController controller;

    private Timer dasTimer;
    private int dasDirection;

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
                break;
        }
    }

    private void handleDirectionalKeyPressed(int direction) {
        if (dasDirection == direction) {
            // OSのキーリピートイベントで同方向が連続発火するのを無視する
            return;
        }

        stopDas();
        dasDirection = direction;
        moveByDirection(direction);

        dasTimer = new Timer(Constants.DAS_DELAY_MS, e -> startAutoRepeat(direction));
        dasTimer.setRepeats(false);
        dasTimer.start();
    }

    private void startAutoRepeat(int direction) {
        dasTimer = new Timer(Constants.DAS_REPEAT_INTERVAL_MS, e -> moveByDirection(direction));
        dasTimer.start();
    }

    private void moveByDirection(int direction) {
        if (direction < 0) {
            controller.moveLeft();
        } else {
            controller.moveRight();
        }
    }

    private void stopDas() {
        if (dasTimer != null) {
            dasTimer.stop();
            dasTimer = null;
        }
        dasDirection = 0;
    }
}

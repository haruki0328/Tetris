package com.tetris;

import com.tetris.view.GameFrame;

/**
 * テトリスアプリケーションのエントリーポイント。
 */
public final class Main {

    /**
     * インスタンス化を防止するためのプライベートコンストラクタ。
     */
    private Main() {
        throw new AssertionError("Main クラスはインスタンス化できません。");
    }

    /**
     * アプリケーションを起動する。
     *
     * @param args コマンドライン引数（未使用）
     */
    public static void main(String[] args) {
        GameFrame.launch();
    }
}

package com.tetris;

import com.tetris.view.GameFrame;

public final class Main {

    private Main() {
        throw new AssertionError("Main クラスはインスタンス化できません。");
    }

    public static void main(String[] args) {
        GameFrame.launch();
    }
}

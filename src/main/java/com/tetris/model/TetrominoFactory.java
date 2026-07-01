package com.tetris.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 7-bagアルゴリズムに基づき、テトリミノ種別をランダムに供給するファクトリクラス。
 *
 * <p>7-bagアルゴリズムは、7種類のテトリミノを1つの「袋」に入れてシャッフルし、
 * 袋が空になるまで順に取り出す方式。1周期（7個）の中で必ず全種類が
 * 1回ずつ出現するため、同じミノが連続で偏って出現することを防ぐ。</p>
 */
public final class TetrominoFactory {

    private final Random random;
    private final List<TetrominoType> currentBag;

    /**
     * デフォルトの乱数生成器を用いてファクトリを生成する。
     */
    public TetrominoFactory() {
        this(new Random());
    }

    /**
     * 指定した乱数生成器を用いてファクトリを生成する。
     * <p>テスト時に再現性のあるシード値を指定する用途を想定する。</p>
     *
     * @param random 使用する乱数生成器
     */
    public TetrominoFactory(Random random) {
        this.random = random;
        this.currentBag = new ArrayList<>();
        refillBag();
    }

    /**
     * 袋の中身が尽きた場合に、7種類のテトリミノを新たに詰めてシャッフルする。
     */
    private void refillBag() {
        List<TetrominoType> freshSet = new ArrayList<>(Arrays.asList(TetrominoType.values()));
        Collections.shuffle(freshSet, random);
        currentBag.addAll(freshSet);
    }

    /**
     * 次のテトリミノ種別を1つ取り出す。
     * <p>袋が空の場合は自動的に補充してから取り出す。</p>
     *
     * @return 次に出現させるテトリミノ種別
     */
    public TetrominoType next() {
        if (currentBag.isEmpty()) {
            refillBag();
        }
        return currentBag.remove(0);
    }
}

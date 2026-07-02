package com.tetris.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 7種類のミノを1袋に入れてシャッフルし、順に取り出す7-bag方式。
// 1周期の中で必ず全種類が1回ずつ出るので、同じミノの偏りを防げる。
public final class TetrominoFactory {

    private final Random random;
    private final List<TetrominoType> currentBag;

    public TetrominoFactory() {
        this(new Random());
    }

    public TetrominoFactory(Random random) {
        this.random = random;
        this.currentBag = new ArrayList<>();
        refillBag();
    }

    private void refillBag() {
        List<TetrominoType> freshSet = new ArrayList<>(Arrays.asList(TetrominoType.values()));
        Collections.shuffle(freshSet, random);
        currentBag.addAll(freshSet);
    }

    public TetrominoType next() {
        if (currentBag.isEmpty()) {
            refillBag();
        }
        return currentBag.remove(0);
    }
}

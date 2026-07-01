package com.tetris.model;

/**
 * ボード上の2次元座標（列, 行）を表す不変(immutable)クラス。
 *
 * <p>x はボードの列（横方向）、y はボードの行（縦方向、下に向かって増加）を表す。</p>
 */
public final class Point {

    private final int x;
    private final int y;

    /**
     * 指定した座標で {@code Point} を生成する。
     *
     * @param x 列位置
     * @param y 行位置
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 列位置（x座標）を取得する。
     *
     * @return 列位置
     */
    public int getX() {
        return x;
    }

    /**
     * 行位置（y座標）を取得する。
     *
     * @return 行位置
     */
    public int getY() {
        return y;
    }

    /**
     * この座標を指定量だけ平行移動した新しい {@code Point} を返す。
     *
     * <p>このクラスは不変であるため、自身は変更されない。</p>
     *
     * @param dx x方向の移動量
     * @param dy y方向の移動量
     * @return 移動後の新しい {@code Point}
     */
    public Point translate(int dx, int dy) {
        return new Point(this.x + dx, this.y + dy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}

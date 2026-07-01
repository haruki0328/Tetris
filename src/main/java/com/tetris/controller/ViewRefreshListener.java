package com.tetris.controller;

/**
 * {@link GameController} の状態が更新された際に、Viewへ再描画を促すためのリスナー。
 *
 * <p>Model層の {@code GameEventListener} とは異なり、こちらは「タイマーによる
 * 自動落下」や「キー入力による移動」など、Controllerが検知したあらゆる変化に対して
 * 再描画のトリガーとして呼び出される、より高頻度な通知を目的とする。</p>
 */
public interface ViewRefreshListener {

    /**
     * 画面の再描画が必要なタイミングで呼び出される。
     */
    void onRefresh();
}

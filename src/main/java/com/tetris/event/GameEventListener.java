package com.tetris.event;

/**
 * Model層で発生したゲームイベントを受け取るリスナーインターフェース。
 *
 * <p>Observerパターンにより、Model（{@code Board} など）は View や Controller の
 * 具体的なクラスを知ることなく、状態変化を通知できる。</p>
 */
public interface GameEventListener {

    /**
     * ゲームイベントが発生した際に呼び出される。
     *
     * @param event 発生したイベントの種別
     */
    void onGameEvent(GameEvent event);
}

package com.tetris.event;

/**
 * Model層で発生し、View/Controllerへ通知されるゲームイベントの種別。
 */
public enum GameEvent {

    /** テトリミノがボードに固定された。 */
    PIECE_LOCKED,

    /** 1行以上のラインが削除された。 */
    LINES_CLEARED,

    /** 新しいテトリミノが出現した。 */
    PIECE_SPAWNED,

    /** ゲームオーバーになった。 */
    GAME_OVER
}

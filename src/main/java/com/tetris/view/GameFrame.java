package com.tetris.view;

import com.tetris.constants.Constants;
import com.tetris.controller.GameController;
import com.tetris.controller.KeyInputHandler;
import com.tetris.model.Board;
import com.tetris.model.GameState;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * テトリスアプリケーションのメインウィンドウ。
 *
 * <p>Model（{@link Board}, {@link GameState}）、Controller（{@link GameController},
 * {@link KeyInputHandler}）、View（{@link BoardPanel}, {@link SidePanel}）を
 * このクラスで結線し、アプリケーション全体を起動する。</p>
 */
public final class GameFrame extends JFrame {

    /**
     * MVCの各コンポーネントを生成・結線し、ウィンドウを構築する。
     */
    public GameFrame() {
        super(Constants.WINDOW_TITLE);

        // --- Model ---
        Board board = new Board();
        GameState gameState = new GameState();

        // --- Controller ---
        GameController controller = new GameController(board, gameState);

        // --- View ---
        BoardPanel boardPanel = new BoardPanel(board, controller, gameState);
        SidePanel sidePanel = new SidePanel(controller, gameState);

        // ControllerからViewへの更新通知（Observer）を結線する。
        // ゲーム状態が変化するたびに両パネルを再描画する。
        controller.addRefreshListener(() -> {
            boardPanel.repaint();
            sidePanel.repaint();
        });

        // --- キー入力の結線 ---
        KeyInputHandler keyInputHandler = new KeyInputHandler(controller);

        setLayout(new java.awt.BorderLayout());
        add(boardPanel, java.awt.BorderLayout.CENTER);
        add(sidePanel, java.awt.BorderLayout.EAST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        addKeyListener(keyInputHandler);
        setFocusable(true);
        requestFocusInWindow();

        controller.start();
    }

    /**
     * アプリケーションのエントリーポイントから呼び出される起動処理。
     * <p>Swingの推奨事項に従い、GUIの構築・表示はイベントディスパッチスレッド上で行う。</p>
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}

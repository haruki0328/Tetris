package com.tetris.view;

import com.tetris.constants.Constants;
import com.tetris.controller.GameController;
import com.tetris.controller.KeyInputHandler;
import com.tetris.model.Board;
import com.tetris.model.GameState;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class GameFrame extends JFrame {

    public GameFrame() {
        super(Constants.WINDOW_TITLE);

        Board board = new Board();
        GameState gameState = new GameState();
        GameController controller = new GameController(board, gameState);

        BoardPanel boardPanel = new BoardPanel(board, controller, gameState);
        SidePanel sidePanel = new SidePanel(controller, gameState);

        controller.addRefreshListener(() -> {
            boardPanel.repaint();
            sidePanel.repaint();
        });

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

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}

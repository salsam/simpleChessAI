package chess.gui;

import chess.gui.actionlisteners.ChessBoardListener;
import chess.gui.boarddrawing.ChessBoardDrawer;
import chess.domain.GameSituation;
import chess.logic.inputprocessing.InputProcessor;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 *
 * @author sami
 */
public class GameWindow extends JFrame {

    private GameSituation game;
    private InputProcessor inputProcessor;
    private JLabel textArea;

    public GameWindow(InputProcessor inputProcessor, GameSituation game) {
        this.game = game;
        this.inputProcessor = inputProcessor;
        this.setPreferredSize(new Dimension(450, 550));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        createComponents(this.getContentPane());
        this.pack();
        this.setVisible(false);
    }

    public GameSituation getGame() {
        return game;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    private void createComponents(Container container) {
        textArea = new JLabel(game.whoseTurn() + "'s turn.");
        textArea.setFont(new Font("Serif", Font.PLAIN, 20));
        textArea.setMaximumSize(new Dimension(300, 100));
        textArea.setAlignmentX(CENTER_ALIGNMENT);
        textArea.setAlignmentY(TOP_ALIGNMENT);
        inputProcessor.setTextArea(textArea);

        ChessBoardDrawer window = new ChessBoardDrawer(inputProcessor, game, 50);
        window.setMaximumSize(new Dimension(400, 400));
        window.setAlignmentX(CENTER_ALIGNMENT);
        window.setAlignmentY(CENTER_ALIGNMENT);
        window.addMouseListener(new ChessBoardListener(inputProcessor, window, 50));

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(textArea);
        container.add(window);
    }

    @Override
    public void repaint() {
        textArea.setText(game.whoseTurn() + "'s turn.");
        super.repaint();
    }

}

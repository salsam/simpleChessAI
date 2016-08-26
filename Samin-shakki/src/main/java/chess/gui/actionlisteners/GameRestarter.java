package chess.gui.actionlisteners;

import chess.gui.GameWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author sami
 */
public class GameRestarter implements ActionListener {

    private Map<String, JFrame> frames;

    public GameRestarter(Map<String, JFrame> frames) {
        this.frames = frames;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        GameWindow gameWindow = (GameWindow) frames.get("game");
        gameWindow.getGame().reset();
        gameWindow.repaint();
        frames.get("endingScreen").setVisible(false);
    }

}

package chess.gui.actionlisteners;

import chess.gui.GameWindow;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author sami
 */
public class GameRestarter implements MouseListener {

    private Map<String, JFrame> frames;

    public GameRestarter(Map<String, JFrame> frames) {
        this.frames = frames;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GameWindow gameWindow = (GameWindow) frames.get("game");
        gameWindow.getGame().reset();
        gameWindow.repaint();
        frames.get("endingScreen").setVisible(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}

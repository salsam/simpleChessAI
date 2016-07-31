package chess.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author sami
 */
public class AiVsAiGameStarter implements ActionListener {

    private MainFrame main;

    public AiVsAiGameStarter(MainFrame main) {
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        main.setVisible(false);
        main.getGameWindow().getGame().setBlackAI(true);
        main.getGameWindow().getGame().setWhiteAI(true);
        main.getGameWindow().setVisible(true);
    }
}

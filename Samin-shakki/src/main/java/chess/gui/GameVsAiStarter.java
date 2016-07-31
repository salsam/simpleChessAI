package chess.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author sami
 */
public class GameVsAiStarter implements ActionListener {

    private MainFrame main;
    
    public GameVsAiStarter(MainFrame main) {
        this.main = main;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        main.setVisible(false);
        main.getGameWindow().getGame().setBlackAI(true);
        main.getGameWindow().setVisible(true);
    }
}

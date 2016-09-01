package chess.gui.actionlisteners;

import chess.domain.GameSituation;
import chess.gui.GameWindow;
import chess.logic.ailogic.AILogic;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
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
        boolean[] ais = gameWindow.getGame().getAis();
        gameWindow.setGame(new GameSituation(new StandardBoardInitializer(), new MovementLogic()));
        gameWindow.getGame().getAis()[0] = ais[0];
        gameWindow.getGame().getAis()[1] = ais[1];
        AILogic[] ai = gameWindow.getInputProcessor().getAis();
        long temp = ai[0].getTimeLimit();
        ai[0] = new AILogic();
        ai[0].setTimeLimit(temp);
        temp=ai[1].getTimeLimit();
        ai[1] = new AILogic();
        ai[1].setTimeLimit(temp);
        gameWindow.repaint();
        frames.get("endingScreen").setVisible(false);
    }

}

package chess.gui;

import chess.domain.GameSituation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 *
 * @author sami
 */
public class DifficultySetter implements ActionListener {
    
    private MainFrame mf;
    private JTextField text;
    
    public DifficultySetter(MainFrame mf, JTextField text) {
        this.mf = mf;
        this.text = text;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        long timeLimit = Long.parseLong(text.getText());
        mf.getGameWindow().getInputProcessor().setAiDifficulty(timeLimit);
    }
    
}

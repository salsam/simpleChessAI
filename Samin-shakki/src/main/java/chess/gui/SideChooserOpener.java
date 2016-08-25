package chess.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author sami
 */
public class SideChooserOpener implements ActionListener {
    
    private SideChooser sc;
    private MainFrame mf;
    
    public SideChooserOpener(MainFrame mf, SideChooser sc) {
        this.mf = mf;
        this.sc = sc;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        mf.setVisible(false);
        sc.setVisible(true);
    }
}

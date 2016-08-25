package chess.gui;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author sami
 */
public class SideChooser extends JFrame {

    public SideChooser(MainFrame main) {
        this.setPreferredSize(new Dimension(450, 300));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents(this.getContentPane(), main);
        this.pack();
        this.setVisible(false);
    }

    private void initComponents(Container container, MainFrame main) {
        JLabel label = new JLabel("Choose which side you'd like to play.");
        ButtonGroup bg = new ButtonGroup();
        JRadioButton black = new JRadioButton("Black");
        JRadioButton white = new JRadioButton("White");
        bg.add(black);
        bg.add(white);

        JPanel adc = createAiDifficultyChooser(main);
        adc.setPreferredSize(new Dimension(300, 100));
        adc.setAlignmentX(CENTER_ALIGNMENT);

        JButton start = new JButton("Start game");
        start.addActionListener(new GameVsAiStarter(main, this, black));

        container.setPreferredSize(new Dimension(450, 300));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(label);
        container.add(black);
        container.add(white);
        container.add(adc);
        container.add(start);
    }

    private JPanel createAiDifficultyChooser(MainFrame main) {
        JPanel adc = new JPanel();
        JLabel label = new JLabel("Write how long (in milliseconds) can ai think each turn.");
        JTextField tf = new JTextField("1000");
        tf.setPreferredSize(new Dimension(100, 100));
        JButton ok = new JButton("Ok");
        ok.addActionListener(new DifficultySetter(main, tf));
        this.add(label);
        this.add(tf);
        this.add(ok);
        return adc;
    }

}

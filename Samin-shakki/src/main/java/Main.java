
import chess.domain.datastructures.MyHashSet;
import java.util.Iterator;

/**
 *
 * @author sami
 */
public class Main {

    public static void main(String[] args) {
//        GameSituation game = new GameSituation(new StandardBoardInitializer(), new MovementLogic());
//        InputProcessor guiLogic = new InputProcessor();
//        GraphicalUserInterface gui = new GraphicalUserInterface(guiLogic, game);
//        gui.run();
        MyHashSet<Integer> mhs = new MyHashSet();
        for (int i = 0; i < 12; i++) {
            mhs.add(i);
        }

        for (int i = 0; i < 12; i++) {
            if (mhs.contains(i)) {
                System.out.println(i);
            } else {
                System.out.println("------" + i);
            }
        }
    }
}

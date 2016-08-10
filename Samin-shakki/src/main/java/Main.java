
import chess.domain.datastructures.MyHashMap;

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

        MyHashMap<Integer, Integer> test = new MyHashMap();
        test.put(1, 2);
        System.out.println(test.containsKey(1));
        System.out.println(test.containsValue(2));
        test.remove(1);
        System.out.println(test.containsKey(1));
        System.out.println(test.containsValue(2));
    }
}

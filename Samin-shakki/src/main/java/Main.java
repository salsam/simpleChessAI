
import chess.domain.GameSituation;
import chess.gui.GraphicalUserInterface;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.logic.inputprocessing.InputProcessor;
import chess.logic.movementlogic.MovementLogic;

/**
 *
 * @author sami
 */
public class Main {

    public static void main(String[] args) {
        GameSituation game = new GameSituation(new StandardBoardInitializer(), new MovementLogic());
        InputProcessor guiLogic = new InputProcessor();
        GraphicalUserInterface gui = new GraphicalUserInterface(guiLogic, game);
        gui.run();
    }
}

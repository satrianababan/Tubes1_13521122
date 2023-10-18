import javafx.scene.control.Button;
import java.util.*;

abstract class Bot {
    abstract int[] move(Button[][] buttons, int roundsLeft, int playerOScore, int playerXScore, boolean playerXTurn);
}


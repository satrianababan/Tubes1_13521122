import javafx.scene.control.Button;
import java.util.*;

public class Bot {
    public int[] move(Button[][] buttons, int roundsLeft, int playerOScore, int playerXScore, boolean playerXTurn) {

        Button[][] botButtons = copyButton(buttons);

        // /* SIDEWAYS HILL CLIMB */
        int[] res = hillClimbingSideWays(botButtons);

        /* STEEPEST ASCENT HILL CLIMB */
        // int[] res = hillClimbingSteepestAscent(botButtons);

        /* STOCHASTIC HILL CLIMB */
        // int[] res = hillClimbingStochastic(botButtons);

        /* SIMULATED ANNEALING */
        // int[] res = simulatedAnnealing(botButtons);

        return new int[]{res[0],res[1]};
    }

    public int[] hillClimbingSideWays(Button[][] buttons) {
        int[][] buttonValues = new int[buttons.length][buttons.length];
        Button[][] currentSolution = copyButton(buttons);
        int currentValue = calculatePoint(currentSolution);

        List<int[]> neighbors = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (buttons[i][j].getText().equals("")) {
                    Button[][] buttonTemp = updateGameBoard(i, j, currentSolution, false);
                    buttonValues[i][j] = calculatePoint(buttonTemp);
                    neighbors.add(new int[]{i, j});
                } else {
                    buttonValues[i][j] = 0;
                }
            }
        }

        List<int[]> bestMoves = new ArrayList<>();
        int highestValue = currentValue;

        for (int[] neighbor : neighbors) {
            int i = neighbor[0];
            int j = neighbor[1];
            if (buttonValues[i][j] >= highestValue) {
                if (buttonValues[i][j] > highestValue) {
                    bestMoves.clear();
                    highestValue = buttonValues[i][j];
                }
                bestMoves.add(new int[]{i, j});
            }
        }

        if (!bestMoves.isEmpty()) {
            int[] res = bestMoves.get((int) (Math.random() * bestMoves.size()));
            currentSolution = updateGameBoard(res[0], res[1], currentSolution, false);
            return res;
        }

        // If no strictly better moves are available, consider moving to a worse move with a probability
        for (int[] neighbor : neighbors) {
            int i = neighbor[0];
            int j = neighbor[1];
            int delta = buttonValues[i][j] - currentValue;
            if (delta < 0 || Math.random() < Math.exp(-delta / 0.1)) {
                currentSolution = updateGameBoard(i, j, currentSolution, false);
                return new int[]{i, j};
            }
        }

        return new int[]{-1, -1}; // Return a sentinel value to indicate no valid moves.
    }

    public int[] hillClimbSteepestAscent(Button[][] buttons) {
        int[][] buttonValues = new int[buttons.length][buttons.length];
        Button[][] currentSolution = copyButton(buttons);
        int currentValue = calculatePoint(currentSolution);

        List<int[]> neighbors = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (buttons[i][j].getText().equals("")) {
                    Button[][] buttonTemp = updateGameBoard(i, j, currentSolution, false);
                    buttonValues[i][j] = calculatePoint(buttonTemp);
                    neighbors.add(new int[]{i, j});
                } else {
                    buttonValues[i][j] = 0;
                }
            }
        }

        List<int[]> bestMoves = new ArrayList<>();
        int highestValue = currentValue;

        for (int[] neighbor : neighbors) {
            int i = neighbor[0];
            int j = neighbor[1];
            if (buttonValues[i][j] >= highestValue) {
                bestMoves.clear();
                highestValue = buttonValues[i][j];
                bestMoves.add(new int[]{i, j});
            }
        }

        if (!bestMoves.isEmpty()) {
            int[] res = bestMoves.get((int) (Math.random() * bestMoves.size()));
            currentSolution = updateGameBoard(res[0], res[1], currentSolution, false);
            return res;
        }

        // If no strictly better moves are available, consider moving to a worse move with a probability
        for (int[] neighbor : neighbors) {
            int i = neighbor[0];
            int j = neighbor[1];
            int delta = buttonValues[i][j] - currentValue;
            if (delta < 0 || Math.random() < Math.exp(-delta / 0.1)) {
                currentSolution = updateGameBoard(i, j, currentSolution, false);
                return new int[]{i, j};
            }
        }

        return new int[]{-1, -1}; // Return a sentinel value to indicate no valid moves.
    }

    public int[] hillClimbingStochastic(Button[][] buttons) {
        int[][] buttonValues = new int[buttons.length][buttons.length];
        int[] res = new int[2];
        int highestValue = 0;

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (buttons[i][j].getText().equals("")) {
                    Button[][] buttonTemp = updateGameBoard(i, j, buttons, false);
                    buttonValues[i][j] = calculatePoint(buttonTemp);

                    // Check if the current value is the same as the highest value
                    if (buttonValues[i][j] == highestValue) {
                        // Randomly choose whether to update the result
                        if (Math.random() < 0.5) {
                            res = new int[]{i, j};
                        }
                    }
                    // Check if the current value is higher than the highest value
                    else if (buttonValues[i][j] > highestValue) {
                        res = new int[]{i, j};
                        highestValue = buttonValues[i][j];
                    }
                }
            }
        }
        return res;
    }

    public int[] simulatedAnnealing(Button[][] buttons) {
        Button[][] currentSolution = copyButton(buttons);
        Button[][] bestSolution = copyButton(buttons);

        double initialTemperature = 100.0;  // Suhu awal
        double coolingRate = 0.03;  // Tingkat pendinginan

        double currentEnergy = calculatePoint(currentSolution);
        double bestEnergy = currentEnergy;

        while (initialTemperature > 1) {
            int randomRow = (int) (Math.random() * 8);
            int randomCol = (int) (Math.random() * 8);

            Button[][] newSolution = updateGameBoard(randomRow, randomCol, currentSolution, false);
            double newEnergy = calculatePoint(newSolution);

            double energyDifference = newEnergy - currentEnergy;

            if (energyDifference < 0 || Math.random() < Math.exp(-energyDifference / initialTemperature)) {
                currentSolution = copyButton(newSolution);
                currentEnergy = newEnergy;

                if (currentEnergy < bestEnergy) {
                    bestSolution = copyButton(currentSolution);
                    bestEnergy = currentEnergy;
                }
            }

            initialTemperature *= coolingRate;
        }

        int[] res = new int[2];
        int highestValue = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (buttons[i][j].getText().equals("")) {
                    int[] move = new int[]{i, j};
                    int moveValue = calculatePoint(updateGameBoard(move[0], move[1], bestSolution, false));
                    if (moveValue >= highestValue) {
                        res = move;
                        highestValue = moveValue;
                    }
                }
            }
        }

        return res;
    }



    private int calculatePoint(Button[][] buttons)
    {
        int res = 0;
        for (int i = 0; i < buttons.length ; i++)
        {
            for (int j = 0; j < buttons[0].length; j++)
            {
                if(buttons[i][j].getText().equals("O"))
                {
                    res++;
                }
                else if (buttons[i][j].getText().equals("X"))
                {
                    res--;
                }

            }
        }
        return res;
    }

    public Button[][] copyButton(Button[][] buttons)
    {
        Button[][] copiedButton = new Button[8][8];
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                copiedButton[i][j] = new Button(buttons[i][j].getText());
            }
        }
        return copiedButton;
    }

    private Button[][] updateGameBoard(int i, int j,Button[][] buttonsExternal, boolean playerXTurn)
    {
        Button[][] buttons = copyButton(buttonsExternal);
        // Value of indices to control the lower/upper bound of rows and columns
        // in order to change surrounding/adjacent X's and O's only on the game board.
        // Four boundaries:  First & last row and first & last column.

        int startRow, endRow, startColumn, endColumn;

        if (i - 1 < 0)     // If clicked button in first row, no preceding row exists.
            startRow = i;
        else               // Otherwise, the preceding row exists for adjacency.
            startRow = i - 1;

        if (i + 1 >= 8)  // If clicked button in last row, no subsequent/further row exists.
            endRow = i;
        else               // Otherwise, the subsequent row exists for adjacency.
            endRow = i + 1;

        if (j - 1 < 0)     // If clicked on first column, lower bound of the column has been reached.
            startColumn = j;
        else
            startColumn = j - 1;

        if (j + 1 >= 8)  // If clicked on last column, upper bound of the column has been reached.
            endColumn = j;
        else
            endColumn = j + 1;


        // Search for adjacency for X's and O's or vice versa, and replace them.
        // Update scores for X's and O's accordingly.
        for (int x = startRow; x <= endRow; x++) {
//            System.out.println("x"+x);
            buttons = this.setPlayerScore(x, j, buttons, playerXTurn);
        }

        for (int y = startColumn; y <= endColumn; y++) {
//            System.out.println("y"+y);
            buttons = this.setPlayerScore(i, y, buttons, playerXTurn);
        }

        return buttons;
    }

    private Button[][] setPlayerScore(int i, int j, Button[][] buttonsExternal, boolean playerXTurn)
    {
        Button[][] buttonsCopy = copyButton(buttonsExternal);
        if (playerXTurn)
        {
            if (buttonsCopy[i][j].getText().equals("O"))
            {
                buttonsCopy[i][j].setText("X");
            }
        } else if (buttonsCopy[i][j].getText().equals("X"))
        {
            buttonsCopy[i][j].setText("O");
        }
        return buttonsCopy;
    }
}


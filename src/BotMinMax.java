import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.control.Button;

public class BotMinMax extends Bot{
    private static final int INFINITY = 9999;

    private Node node;

    private int ROW;
    private int COL;

    private Button[][] buttons;

    private String bot;
    private String opponent;

    private int botScore;
    private int[] bestMove;
    public BotMinMax(int row, int col, Button[][] buttons, String bot) {
        this.ROW = row;
        this.COL = col;
        this.buttons = buttons;
        this.bot = bot;
        this.opponent = bot.equals("O") ? "X" : "O";
    }

    public void updateScore(int botScore){

        this.botScore = botScore;
    }


    public int[] move(Button[][] buttons, int roundsLeft, int playerOScore, int playerXScore, boolean playerXTurn) {
        this.node = new Node();
        initializeBoard();

        int depth = 4;
        long startTime = System.currentTimeMillis();
        long timeout = 5000;

        int[] bestMove = randomMove(this.node);

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= timeout) {
                break;  // Exit the loop
            }

            Node tempNode = new Node(this.node);  // Create a temporary node to avoid modifying the current node
            minimax(tempNode, currentDepth, roundsLeft, -INFINITY, INFINITY, true);

            // Update the best move
            for (Node child : tempNode.getChildren()) {
                if (child.getValue() == tempNode.getValue()) {
                    bestMove = child.getMove();
                }
            }
        }

        System.out.println(bestMove[0] + ", " + bestMove[1]);
        return bestMove;
    }

    private void minimax(Node node, int depth, int roundsLeft, int alpha, int beta, boolean maxPlayer) {
        if(depth == 0 || roundsLeft == 0) {
            node.setTerminalValue(this.bot);
            return;
        }

        if(maxPlayer) {
            addHeuristicMove(this.bot, node);

            if(node.getChildren().isEmpty()) {
                node.setTerminalValue(this.bot);
                return;
            }

            node.setValue(-INFINITY);
            for (Node child : node.getChildren()) {
                minimax(child, depth-1, roundsLeft-1, alpha, beta, false);
                node.setValue(Math.max(child.getValue(), node.getValue()));
                alpha = Math.max(child.getValue(), alpha);
                if(beta <= alpha) break;
            }
        }

        else {
            addHeuristicMove(this.opponent, node);

            if(node.getChildren().isEmpty()) {
                node.setTerminalValue(this.bot);
                return;
            }

            node.setValue(INFINITY);
            for (Node child : node.getChildren()) {
                minimax(child, depth-1, roundsLeft-1, alpha, beta, true);
                node.setValue(Math.min(child.getValue(), node.getValue()));
                beta = Math.min(child.getValue(), beta);
                if(beta <= alpha) break;
            }
        }
    }






    private void initializeBoard() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (!buttons[i][j].getText().isEmpty()) {
                    this.node.writeNodeBoard(i, j, buttons[i][j].getText());
                }
            }
        }
    }

    private void addHeuristicMove(String botMarker, Node node) {
        String opponentMarker = botMarker.equals("O") ? "X" : "O";
        int[] move = randomMove(node);

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (node.getNodeBoard()[i][j].equals("")) {
                    move = new int[]{i, j};
                    Node child = new Node(node);
                    child.writeNodeBoard(move[0], move[1], this.bot);
                    child.setMove(move[0], move[1]);
                    node.addChild(child);
                }
            }
        }
    }
    public int[] randomMove(Node node) {
        List<int[]> validMove = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++) {
                if(node.getNodeBoard()[i][j].equals("")) {
                    validMove.add(new int[]{i, j});
                }
            }
        }
        int rand_index = rand.nextInt(validMove.size());

        return  validMove.get(rand_index);
    }
}

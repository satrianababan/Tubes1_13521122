import java.util.ArrayList;
import java.util.List;

public class Node {
    private static final int ROW = 8;
    private static final int COL = 8;

    private String[][] nodeBoard;

    private int[] move;

    private int value;

    private List<Node> children;

    public Node() {
        this.nodeBoard = new String[ROW][COL];
        this.value = 0;
        this.children = new ArrayList<>();
        this.move = new int[]{0, 0};

        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++) {
                this.nodeBoard[i][j] = "";
            }
        }
    }

    public Node(Node parent) {
        this.nodeBoard = new String[ROW][COL];
        this.value = 0;
        this.children = new ArrayList<>();
        this.move = new int[]{0, 0};

        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++) {
                this.nodeBoard[i][j] = parent.getNodeBoard()[i][j];
            }
        }
    }

    // Setter
    public void writeNodeBoard(int row, int col, String botMarker) {
        String opponentMarker = botMarker.equals("O") ? "X" : "O";

        if(row != 0 && this.nodeBoard[row-1][col].equals(opponentMarker)) {
            this.nodeBoard[row-1][col] = botMarker;
        }

        if(row != ROW - 1 && this.nodeBoard[row+1][col].equals(opponentMarker)) {
            this.nodeBoard[row+1][col] = botMarker;
        }

        if(col != 0 && this.nodeBoard[row][col-1].equals(opponentMarker)) {
            this.nodeBoard[row][col-1] = botMarker;
        }

        if(col != COL - 1 && this.nodeBoard[row][col+1].equals(opponentMarker)) {
            this.nodeBoard[row][col+1] = botMarker;
        }

        this.nodeBoard[row][col] = botMarker;
    }

    public void setTerminalValue(String botMarker) {
        for(int i  = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++) {
                if(this.nodeBoard[i][j].equals(botMarker)) {
                    this.value++;
                }
            }
        }
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setMove(int row, int col) {
        this.move[0] = row;
        this.move[1] = col;
    }

    // Getter
    public List<Node> getChildren() {
        return this.children;
    }

    public int getValue() {
        return this.value;
    }

    public String[][] getNodeBoard() {
        return this.nodeBoard;
    }

    public int[] getMove() {
        return this.move;
    }
}
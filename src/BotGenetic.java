import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.Button;

public class BotGenetic extends Bot{
    private Button[][] buttons;

    public BotGenetic(Button[][] buttons){
        this.buttons = buttons;
    }

    public int[] move(Button[][] buttons, int roundsLeft, int playerOScore, int playerXScore, boolean playerXTurn) {
        ArrayList<ArrayList<Integer>> unFilled = new ArrayList<>();

        for(int i= 0; i< 8; i++){
            for(int j= 0; j< 8; j++){
                if(buttons[i][j].getText().equals("")){
                    unFilled.add(new ArrayList<>(Arrays.asList(i, j)));
                }
            }
        }
        if(unFilled.size() < 10){
            int random = (int) (Math.random() * unFilled.size());
            return new int[]{unFilled.get(random).get(0), unFilled.get(random).get(1)};
        }
        ArrayList<ArrayList<Integer>> genePool = getGenePool(5, 20);
        ArrayList<Integer> fitness = calculateFitness(genePool, true);

        ArrayList<Integer> wheel = createWheel(fitness);

        ArrayList<ArrayList<Integer>> children = new ArrayList<>();
        for(int i = 0; i < 200; i++){
            ArrayList<Integer> parrent1 = genePool.get(getOneParrent(wheel));
            ArrayList<Integer> parrent2 = genePool.get(getOneParrent(wheel));

            int crossOverPoint = (int) (Math.random() * parrent1.size());
            ArrayList<ArrayList<Integer>> crossOver = crossOver(parrent1, parrent2, crossOverPoint);


            int mutationPoint = (int) (Math.random() * parrent1.size());
            mutation(crossOver.get(0), mutationPoint);
            mutation(crossOver.get(1), mutationPoint);
            children.add(crossOver.get(0));
            children.add(crossOver.get(1));
        }

        ArrayList<Integer> fitnessSolution = calculateFitness(children, false);

        int max = -99999;
        int indexMax = -1;
        for(int i = 0; i < fitnessSolution.size(); i++){
            if(fitnessSolution.get(i) > max){
                indexMax = i;
            }
        }

        int bestSolution = children.get(indexMax).get(0);
        int row = bestSolution / 8;
        int col = bestSolution % 8;

        return new int[]{row, col};
    }


    // public .
    public ArrayList<Integer> getOneChromosome(int length){
        ArrayList<Integer> Chromosome = new ArrayList<>();
        for (int j = 0; j<length; j++){
            int tile = (int) (Math.random()*64);
            int row = tile / 8;
            int col = tile % 8;
            while(!this.buttons[row][col].getText().equals("") || Chromosome.contains(tile)){
                // System.out.println("masuk");
                tile = (int) (Math.random()*64);
                row = tile / 8;
                col = tile % 8;
            }
            Chromosome.add(tile);
        }
        return Chromosome;
    }

    public ArrayList<ArrayList<Integer>> getGenePool(int length, int N){
        //length = panjang chromosome atau level pohon
        //N = banyaknya random chromosome
        ArrayList<ArrayList<Integer>> GenePool = new ArrayList<>();
        for(int i = 0; i<N; i++){
            ArrayList<Integer> Chromosome = getOneChromosome(length);
            GenePool.add(Chromosome);
        }
        // System.out.println(GenePool);
        return GenePool;
    }


    public ArrayList<Integer> calculateFitness(ArrayList<ArrayList<Integer>> genePool, boolean isForSelection) {
        ArrayList<Integer> fitness = new ArrayList<Integer>();

        int point = calculatePoint(buttons);
        for(int i=0; i<genePool.size(); i++){
            int temp = -1;
            while(temp <= 0){
                temp = point;
                Button[][] copiedButton = copyButton(buttons);
                for(int j = 0; j < genePool.get(i).size(); j++) {
                    if(j%2 == 0){
                        int row = genePool.get(i).get(j) /8;
                        int column = genePool.get(i).get(j) %8;
                        copiedButton[row][column].setText("O");
                        temp ++;
                        if(isValidButton(row-1, column) && copiedButton[row-1][column].getText().equals("X") ){
                            copiedButton[row-1][column].setText("O");
                            temp += 2;
                        }
                        if(isValidButton(row+1, column) && copiedButton[row+1][column].getText().equals("X")){
                            copiedButton[row+1][column].setText("O");
                            temp += 2;
                        }
                        if( isValidButton(row, column-1) && copiedButton[row][column-1].getText().equals("X")){
                            copiedButton[row][column-1].setText("O");
                            temp += 2;
                        }
                        if(isValidButton(row, column+1) && copiedButton[row][column+1].getText().equals("X")){
                            copiedButton[row][column+1].setText("O");
                            temp += 2;
                        }
                    }else{
                        int row = genePool.get(i).get(j) /8;
                        int column = genePool.get(i).get(j) %8;
                        copiedButton[row][column].setText("X");
                        temp --;
                        if(isValidButton(row-1, column) && copiedButton[row-1][column].getText().equals("O")){
                            copiedButton[row-1][column].setText("X");
                            temp -= 2;
                        }
                        if(isValidButton(row+1, column) && copiedButton[row+1][column].getText().equals("O")){
                            copiedButton[row+1][column].setText("X");
                            temp -= 2;
                        }
                        if(isValidButton(row, column-1) && copiedButton[row][column-1].getText().equals("O")){
                            copiedButton[row][column-1].setText("X");
                            temp -= 2;
                        }
                        if(isValidButton(row, column+1) && copiedButton[row][column+1].getText().equals("O")){
                            copiedButton[row][column+1].setText("X");
                            temp -= 2;
                        }
                    }
                }
                if(isForSelection){
                    if(temp <= 0){
                        genePool.set(i, getOneChromosome(genePool.get(0).size()));
                    }
                }else{
                    if(temp <= 0){
                        temp = 9999;
                    }
                }

            }

            // System.out.println(temp);
            if(!isForSelection && temp == 9999){
                temp = 0;
            }
            fitness.add(temp);


        }
        // System.out.println("breakkk");
        // System.out.println(fitness);
        return fitness;
    }

    public ArrayList<Integer> createWheel(ArrayList<Integer> fitness){
        int sum = 0;
        for(int i = 0; i < fitness.size(); i++){
            sum += fitness.get(i);
        }

        ArrayList<Integer> wheel = new ArrayList<Integer>();
        int wheelCount = 0;
        for(int i = 0; i < fitness.size(); i++){
            double percentage = (fitness.get(i)/(sum + 1)) *100;
            wheelCount += percentage;
            wheel.add(wheelCount);
        }
        return wheel;
    }

    public int getOneParrent(ArrayList<Integer> wheel){
        double random = Math.random()*100;

        int index = 0;
        for(int i=0; i< wheel.size(); i++){
            if(random <= wheel.get(i)){
                index = i;
                break;
            }
        }
        return index;
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

    private boolean isValidButton(int i, int j){
        return (i>=0 && i<8) && (j>=0 && j<8);
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

    public ArrayList<ArrayList<Integer>> crossOver(ArrayList<Integer> parrent1, ArrayList<Integer> parrent2, int crossOverPoint){
        ArrayList<ArrayList<Integer>> crossOverResult = new ArrayList<>();
        ArrayList<Integer> crossOver1 = new ArrayList<>();
        ArrayList<Integer> crossOver2 = new ArrayList<>();

        int length = parrent1.size();

        for (int i = 0; i<crossOverPoint; i++){
            int val = parrent1.get(i);
            int row = val / 8;
            int col = val % 8;
            while(!this.buttons[row][col].getText().equals("") || crossOver1.contains(val)){
                val = (int) (Math.random() * 64);
                row = val / 8;
                col = val % 8;
            }
            crossOver1.add(val);
        }
        for (int i = crossOverPoint; i<length; i++){
            int val = parrent2.get(i);
            int row = val / 8;
            int col = val % 8;
            while(!this.buttons[row][col].getText().equals("") || crossOver1.contains(val)){
                val = (int) (Math.random() * 64);
                row = val / 8;
                col = val % 8;
            }
            crossOver1.add(val);
        }

        for (int i = 0; i<crossOverPoint; i++){
            int val = parrent2.get(i);
            int row = val / 8;
            int col = val % 8;
            while(!this.buttons[row][col].getText().equals("") || crossOver2.contains(val)){
                val = (int) (Math.random() * 64);
                row = val / 8;
                col = val % 8;
            }
            crossOver2.add(val);
        }
        for (int i = crossOverPoint; i<length; i++){
            int val = parrent1.get(i);
            int row = val / 8;
            int col = val % 8;
            while(!this.buttons[row][col].getText().equals("") || crossOver2.contains(val)){
                val = (int) (Math.random() * 64);
                row = val / 8;
                col = val % 8;
            }
            crossOver2.add(val);
        }

        crossOverResult.add(crossOver1);
        crossOverResult.add(crossOver2);

        return crossOverResult;
    }

    public void mutation (ArrayList<Integer> child, int mutationPoint){
        int randomVal = (int) (Math.random() * 64);
        int row = randomVal / 8;
        int col = randomVal % 8;
        while(!this.buttons[row][col].getText().equals("") || child.contains(randomVal)){
            randomVal = (int) (Math.random() * 64);
            row = randomVal / 8;
            col = randomVal % 8;
        }

        child.set(mutationPoint, randomVal);
    }
}

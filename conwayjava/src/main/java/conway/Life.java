package conway;

/*
 * Il core di game of life
 * Non ha dipendenze da dispositivi di input/output
 * Non ha regole di controllo del gioco 
 */

public class Life {
    //La struttura
    private int rows;
    private int cols;
    private Grid grid;
    private Grid nextGrid;
 
    public Life( int rowsNum, int colsNum ) {
        this.rows   = rowsNum;
        this.cols   = colsNum;
        this.grid = new Grid(rows, cols);
        this.nextGrid = new Grid(rows, cols);
    }

    public int getRowsNum(){
        return rows;
    }

    public int getColsNum(){
        return cols;
    }

    public Grid getGrid(){
        return grid;
    }

    protected void resetGrids() {
         grid.clear();
         nextGrid.clear();
    }


    protected int countNeighborsLive(int row, int col) {
        int count = 0;
        if (row-1 >= 0) {
            if (grid.getCell(row-1, col).isAlive()) count++;
        }
        if (row-1 >= 0 && col-1 >= 0) {
            if (grid.getCell(row-1, col-1).isAlive()) count++;
        }
        if (row-1 >= 0 && col+1 < cols) {
            if (grid.getCell(row-1, col+1).isAlive()) count++;
        }
        if (col-1 >= 0) {
            if (grid.getCell(row, col-1).isAlive()) count++;
        }
        if (col+1 < cols) {
            if (grid.getCell(row, col+1).isAlive()) count++;
        }
        if (row+1 < rows) {
            if (grid.getCell(row+1, col).isAlive()) count++;
        }
        if (row+1 < rows && col-1 >= 0) {
            if (grid.getCell(row+1, col-1).isAlive()) count++;
        }
        if (row+1 < rows && col+1 < cols) {
            if (grid.getCell(row+1, col+1).isAlive()) count++;
        }
        return count;
    }


    protected void computeNextGen( IOutDev outdev ) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int n = countNeighborsLive(i,j);
                applyRules(i, j, n);
                outdev.displayCell(grid.getCell(i, j).isAlive() ? "1" : "0");
            }
            outdev.displayCell("\n");  //Va tolta nel caso della GUI?
        }
        copyAndResetGrid( outdev );
        outdev.displayCell("\n");
    }


    protected void copyAndResetGrid( IOutDev outdev ) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid.getCell(i, j).setAlive(nextGrid.getCell(i, j).isAlive());
                //outdev.displayCell( ""+grid[i][j] );
                nextGrid.getCell(i, j).setAlive(false);
            }
        }
    }


    protected void applyRules(int row, int col, int numNeighbors) {
        Cell currentCell = grid.getCell(row, col);
        Cell nextCell = nextGrid.getCell(row, col);
        //int numNeighbors = countNeighborsLive(row, col);

        if (currentCell.isAlive()) { //Cella viva
            nextCell.setAlive(numNeighbors == 3 || numNeighbors == 2); // Se inferiore a 2 muore per isolamento, se superiore a 3 muore per sovra popolazione
        } else {
            nextCell.setAlive(numNeighbors == 3); // Se pari a 3 si riproduce
        }
        //CommUtils.outgreen("Life applyRules " + nextGrid   );
    }


    public void switchCellState(int i, int j){
        grid.getCell(i, j).switchState();
    }

}

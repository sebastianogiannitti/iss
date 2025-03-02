package conway;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    private int rows, cols;
    private List<List<Cell>> cells; // Lista di liste per la griglia

    // Costruttore: inizializza la griglia con celle morte
    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            List<Cell> row = new ArrayList<>(); // Inizializzo la riga
            for (int j = 0; j < cols; j++) {
                row.add(new Cell()); // Aggiunge una cella morta alla riga per ogni colonna
            }
            cells.add(row); // Aggiunge la riga
        }
    }

    // Ottieni una cella specifica
    public Cell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return cells.get(row).get(col);
        }
        return null;
    }

    // Imposta lo stato di una cella (viva/morta)
    public void setCell(int row, int col, boolean alive) {
        if (isValidPosition(row, col)) {
            cells.get(row).get(col).setAlive(alive);
        }
    }

    // Stampa la griglia in console
    public void printGrid() {
        for (List<Cell> row : cells) {
            for (Cell cell : row) {
                System.out.print(cell.toString() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Metodo per ripristinare tutte le celle a uno stato "morto"
    public void clear() {
        for (List<Cell> row : cells) {
            for (Cell cell : row) {
                cell.setAlive(false); // Imposta lo stato della cella a "morta"
            }
        }
    }

    // Metodo per verificare se una posizione è valida
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
package conway;

public class Cell {
    private boolean alive; // true se viva, false se morta

    public Cell() {
        this.alive = false; // inizialmente morta
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean state) {
        this.alive = state;
    }

    // Metodo per alternare lo stato della cella
    public void switchState() {
        this.alive = !this.alive;
    }

    @Override
    public String toString() {
        return alive ? "1" : "0"; // Simboli per rappresentare visivamente lo stato
    }
}

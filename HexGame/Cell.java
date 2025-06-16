package HexGame;

public class Cell {
    private boolean isBlue;
    private boolean isRevealed;
    private boolean isFlagged;
    private int clue;

    public Cell() {
        this.isBlue = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.clue = 0;
    }

    public boolean isBlue() { return isBlue; }
    public void setBlue(boolean blue) { isBlue = blue; }
    public boolean isRevealed() { return isRevealed; }
    public void reveal() { isRevealed = true; }
    public boolean isFlagged() { return isFlagged; }
    public void toggleFlag() { isFlagged = !isFlagged; }
    public int getClue() { return clue; }
    public void setClue(int clue) { this.clue = clue; }
}
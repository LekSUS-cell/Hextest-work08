package HexGame;

public class HexGrid {
    private final int rows;
    private final int cols;

    public HexGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public int[] getNeighbors(int r, int c) {
        int[] neighbors = new int[6];
        for (int i = 0; i < 6; i++) {
            neighbors[i] = -1;
        }

        // Гексагональная сетка, смещение зависит от четности столбца
        int[][] offsets = (c % 2 == 0) ?
                new int[][] {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}} : // Чётный столбец
                new int[][] {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 1}};   // Нечётный столбец

        int index = 0;
        for (int[] offset : offsets) {
            int nr = r + offset[0];
            int nc = c + offset[1];
            if (isValid(nr, nc)) {
                neighbors[index] = nr * cols + nc;
            }
            index++;
        }
        return neighbors;
    }
}
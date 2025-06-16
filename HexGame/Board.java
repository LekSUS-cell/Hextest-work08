package HexGame;

import java.util.Random;

public class Board {
    private final Cell[][] cells;
    private final HexGrid grid;
    private final Random random;
    private final double blueProbability;
    private final boolean[][] isActive;
    private boolean isInitialized;

    public enum Level {
        EASY(5, 5, 0.2, true),
        MEDIUM(7, 7, 0.3, true),
        HARD(9, 9, 0.35, false);

        final int rows;
        final int cols;
        final double blueProbability;
        final boolean isRectangular;

        Level(int rows, int cols, double blueProbability, boolean isRectangular) {
            this.rows = rows;
            this.cols = cols;
            this.blueProbability = blueProbability;
            this.isRectangular = isRectangular;
        }
    }

    public Board(Level level) {
        this.cells = new Cell[level.rows][level.cols];
        this.grid = new HexGrid(level.rows, level.cols);
        this.random = new Random();
        this.blueProbability = level.blueProbability;
        this.isActive = new boolean[level.rows][level.cols];
        this.isInitialized = false;
        initialize(level);
    }

    private void initialize(Level level) {
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (level.isRectangular) {
                    isActive[r][c] = true;
                } else {
                    int midRow = (level.rows - 1) / 2;
                    int midCol = (level.cols - 1) / 2;
                    isActive[r][c] = Math.abs(r - midRow) + Math.abs(c - midCol) <= midRow;
                }
                cells[r][c] = isActive[r][c] ? new Cell() : null;
            }
        }
    }

    public void generateBoardForFirstClick(int startRow, int startCol) {
        if (isInitialized) return;
        isInitialized = true;

        // Делаем стартовую ячейку и её соседей безопасными
        if (isActive[startRow][startCol] && cells[startRow][startCol] != null) {
            cells[startRow][startCol].setBlue(false);
            System.out.println("Стартовая ячейка [" + startRow + "," + startCol + "] безопасная");
        }
        for (int neighbor : grid.getNeighbors(startRow, startCol)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (isActive[nr][nc] && cells[nr][nc] != null) {
                    cells[nr][nc].setBlue(false);
                    System.out.println("Сосед [" + nr + "," + nc + "] безопасный");
                }
            }
        }

        // Подсчитываем активные клетки
        int activeCells = 0;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (isActive[r][c] && cells[r][c] != null) activeCells++;
            }
        }

        // Вычисляем количество синих клеток
        int blueCount = (int) (blueProbability * activeCells);
        int safeCells = 1; // Стартовая ячейка
        for (int neighbor : grid.getNeighbors(startRow, startCol)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (isActive[nr][nc] && cells[nr][nc] != null) safeCells++;
            }
        }
        blueCount = Math.min(blueCount, activeCells - safeCells);
        int placed = 0;

        // Расставляем синие клетки
        while (placed < blueCount) {
            int r = random.nextInt(grid.getRows());
            int c = random.nextInt(grid.getCols());
            if (isActive[r][c] && cells[r][c] != null && !cells[r][c].isBlue() &&
                    !(r == startRow && c == startCol) && !isNeighbor(r, c, startRow, startCol)) {
                cells[r][c].setBlue(true);
                placed++;
                System.out.println("Синяя клетка размещена в [" + r + "," + c + "]");
            }
        }

        // Проверяем стартовую ячейку и соседей
        if (isActive[startRow][startCol] && cells[startRow][startCol].isBlue()) {
            System.err.println("Ошибка: стартовая ячейка [" + startRow + "," + startCol + "] стала синей!");
        }
        for (int neighbor : grid.getNeighbors(startRow, startCol)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (isActive[nr][nc] && cells[nr][nc] != null && cells[nr][nc].isBlue()) {
                    System.err.println("Ошибка: сосед [" + nr + "," + nc + "] стал синим!");
                }
            }
        }

        updateClues();
    }

    private void updateClues() {
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (isActive[r][c] && cells[r][c] != null && !cells[r][c].isBlue()) {
                    int clue = countBlueNeighbors(r, c);
                    cells[r][c].setClue(clue);
                }
            }
        }
    }

    private int countBlueNeighbors(int r, int c) {
        int count = 0;
        for (int neighbor : grid.getNeighbors(r, c)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (isActive[nr][nc] && cells[nr][nc] != null && cells[nr][nc].isBlue()) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isNeighbor(int r, int c, int startRow, int startCol) {
        for (int neighbor : grid.getNeighbors(startRow, startCol)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (r == nr && c == nc) return true;
            }
        }
        return false;
    }

    public Cell getCell(int r, int c) { return cells[r][c]; }
    public HexGrid getGrid() { return grid; }
    public boolean isActive(int r, int c) { return isActive[r][c]; }
}
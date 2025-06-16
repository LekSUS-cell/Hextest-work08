package HexGame;

import java.util.HashSet;
import java.util.Set;

public class Game {
    private final Board board;
    private boolean gameOver;
    private boolean won;
    private int[] hint;
    private boolean isFirstMove;
    private final Set<String> flaggedHints; // Храним ячейки, помеченные как мины

    public Game(Board board) {
        this.board = board;
        this.gameOver = false;
        this.won = false;
        this.hint = null;
        this.isFirstMove = true;
        this.flaggedHints = new HashSet<>();
    }

    public void openFirstCell(int r, int c) {
        if (!board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        if (isFirstMove) {
            board.generateBoardForFirstClick(r, c);
            isFirstMove = false;
        }
        openCell(r, c);
    }

    public void openCell(int r, int c) {
        if (gameOver || !board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        Cell cell = board.getCell(r, c);
        if (cell.isFlagged() || cell.isRevealed()) return;
        cell.reveal();
        hint = null;
        if (cell.isBlue()) {
            gameOver = true;
            won = false;
        } else if (checkWin()) {
            gameOver = true;
            won = true;
        }
    }

    public void toggleFlag(int r, int c) {
        if (gameOver || !board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        Cell cell = board.getCell(r, c);
        if (!cell.isRevealed()) {
            cell.toggleFlag();
            hint = null;
            if (checkWin()) {
                gameOver = true;
                won = true;
            }
        }
    }

    private boolean checkWin() {
        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (!board.isActive(r, c)) continue;
                Cell cell = board.getCell(r, c);
                if (cell.isBlue() && !cell.isFlagged()) return false;
                if (!cell.isBlue() && !cell.isRevealed()) return false;
            }
        }
        return true;
    }

    public int[] getHint() {
        if (hint != null) return hint;

        // Проходим по всем открытым ячейкам
        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (!board.isActive(r, c) || board.getCell(r, c).isBlue() || !board.getCell(r, c).isRevealed()) continue;
                int clue = board.getCell(r, c).getClue();
                int unrevealedNeighbors = 0;
                int flaggedNeighbors = 0;
                int unflaggedNeighbors = 0;
                for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                    if (neighbor == -1) continue;
                    int nr = neighbor / board.getGrid().getCols();
                    int nc = neighbor % board.getGrid().getCols();
                    if (!board.isActive(nr, nc)) continue;
                    if (!board.getCell(nr, nc).isRevealed()) unrevealedNeighbors++;
                    if (board.getCell(nr, nc).isFlagged()) flaggedNeighbors++;
                    if (!board.getCell(nr, nc).isFlagged() && !board.getCell(nr, nc).isRevealed()) unflaggedNeighbors++;
                }

                // Правило 1: clue=0, открыть соседа
                if (clue == 0 && unrevealedNeighbors > 0) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed()) continue;
                        String key = nr + "," + nc;
                        if (!flaggedHints.contains(key)) { // Не противоречит предыдущим минам
                            hint = new int[]{nr, nc, 0}; // Открыть
                            return hint;
                        }
                    }
                }

                // Правило 2: clue равно числу неоткрытых и непомеченных соседей, поставить флаг
                if (unflaggedNeighbors > 0 && clue == unflaggedNeighbors) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed() || board.getCell(nr, nc).isFlagged()) continue;
                        hint = new int[]{nr, nc, 1}; // Поставить флаг
                        flaggedHints.add(nr + "," + nc); // Запоминаем мину
                        return hint;
                    }
                }

                // Правило 3: clue равно числу флагов, открыть остальных соседей
                if (clue == flaggedNeighbors && unrevealedNeighbors > flaggedNeighbors) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed() || board.getCell(nr, nc).isFlagged()) continue;
                        String key = nr + "," + nc;
                        if (!flaggedHints.contains(key)) { // Не противоречит предыдущим минам
                            hint = new int[]{nr, nc, 0}; // Открыть
                            return hint;
                        }
                    }
                }

                // Правило 4: clue минус флаги равно числу оставшихся неоткрытых соседей, поставить флаги
                if (unrevealedNeighbors > flaggedNeighbors && clue - flaggedNeighbors == unrevealedNeighbors - flaggedNeighbors) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed() || board.getCell(nr, nc).isFlagged()) continue;
                        hint = new int[]{nr, nc, 1}; // Поставить флаг
                        flaggedHints.add(nr + "," + nc); // Запоминаем мину
                        return hint;
                    }
                }
            }
        }

        return null;
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isWon() { return won; }
    public Board getBoard() { return board; }
}
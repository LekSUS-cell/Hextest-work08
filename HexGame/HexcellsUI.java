package HexGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class HexcellsUI extends JFrame {
    private final Game game;
    private final HexGrid grid;
    private final Board.Level level;
    private final int hexSize = 30;
    private final double sqrt3 = Math.sqrt(3);
    private double offsetX;
    private double offsetY;
    private HexPanel hexPanel;
    private BufferedImage backgroundImage;

    public HexcellsUI(Board.Level level) {
        this.game = new Game(new Board(level));
        this.grid = game.getBoard().getGrid();
        this.level = level;
        setTitle("Hexcells Infinite - Игра");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(400, 400));

        // Загружаем фоновое изображение
        try {
            backgroundImage = ImageIO.read(new File("background.png"));
        } catch (IOException e) {
            System.err.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
            backgroundImage = null;
        }

        // Панель управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // Кнопка подсказки
        JButton hintButton = new JButton("Подсказка");
        hintButton.addActionListener(e -> {
            int[] hint = game.getHint();
            if (hint != null) {
                hexPanel.setHint(hint);
                hexPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Нет доступных подсказок!");
            }
        });
        controlPanel.add(hintButton);

        // Кнопка меню
        JButton menuButton = new JButton("Меню");
        menuButton.addActionListener(e -> {
            dispose();
            new MainMenu();
        });
        controlPanel.add(menuButton);

        // Основная панель
        hexPanel = new HexPanel();
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(hexPanel, BorderLayout.CENTER);

        // Центрируем окно
        calculateOffsets();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Слушатель изменения размера окна
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateOffsets();
                hexPanel.repaint();
            }
        });
    }

    private void calculateOffsets() {
        int windowWidth = hexPanel.getWidth();
        int windowHeight = hexPanel.getHeight();
        int gridWidth = (int) (grid.getCols() * hexSize * 1.5 + hexSize);
        int gridHeight = (int) (grid.getRows() * hexSize * sqrt3 + hexSize);
        offsetX = Math.max((windowWidth - gridWidth) / 2.0, 0);
        offsetY = Math.max((windowHeight - gridHeight) / 2.0, 0);
    }

    private class HexPanel extends JPanel {
        private int[] hint;

        public HexPanel() {
            setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int[] coords = getHexAt(e.getX(), e.getY());
                    if (coords != null) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            game.openFirstCell(coords[0], coords[1]);
                            if (game.isGameOver() && !game.isWon()) {
                                showGameOverDialog();
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            game.toggleFlag(coords[0], coords[1]);
                        }
                        hint = null;
                        repaint();
                    }
                }
            });
        }

        public void setHint(int[] hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Рисуем фоновое изображение
            if (backgroundImage != null) {
                // Растягиваем изображение под размер панели
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            for (int r = 0; r < grid.getRows(); r++) {
                for (int c = 0; c < grid.getCols(); c++) {
                    if (game.getBoard().isActive(r, c)) {
                        drawHex(g2d, r, c);
                    }
                }
            }

            if (game.isGameOver() && game.isWon()) {
                g2d.setColor(new Color(0, 0, 0, 128));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("Победа!", getWidth() / 2 - 50, getHeight() / 2);
            }
        }

        private void drawHex(Graphics2D g2d, int r, int c) {
            double x = c * hexSize * 1.5 + hexSize + offsetX;
            double y = r * hexSize * sqrt3 + (c % 2 == 0 ? hexSize : hexSize * (sqrt3 / 2 + 1)) + offsetY;
            Path2D hex = new Path2D.Double();
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                double px = x + hexSize * Math.cos(angle);
                double py = y + hexSize * Math.sin(angle);
                if (i == 0) {
                    hex.moveTo(px, py);
                } else {
                    hex.lineTo(px, py);
                }
            }
            hex.closePath();

            Cell cell = game.getBoard().getCell(r, c);
            if (hint != null && hint[0] == r && hint[1] == c) {
                g2d.setColor(hint[2] == 0 ? Color.GREEN : Color.ORANGE);
                g2d.fill(hex);
            } else if (cell.isFlagged()) {
                g2d.setColor(Color.RED);
                g2d.fill(hex);
            } else if (cell.isRevealed()) {
                g2d.setColor(cell.isBlue() ? Color.BLUE : Color.LIGHT_GRAY);
                g2d.fill(hex);
                if (!cell.isBlue()) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(cell.getClue()), (float) x - 5, (float) y + 5);
                }
            } else {
                g2d.setColor(Color.GRAY);
                g2d.fill(hex);
            }
            g2d.setColor(Color.BLACK);
            g2d.draw(hex);
        }

        private int[] getHexAt(int px, int py) {
            for (int r = 0; r < grid.getRows(); r++) {
                for (int c = 0; c < grid.getCols(); c++) {
                    if (!game.getBoard().isActive(r, c)) continue;
                    double x = c * hexSize * 1.5 + hexSize + offsetX;
                    double y = r * hexSize * sqrt3 + (c % 2 == 0 ? hexSize : hexSize * (sqrt3 / 2 + 1)) + offsetY;
                    double dx = px - x;
                    double dy = py - y;
                    if (Math.sqrt(dx * dx + dy * dy) < hexSize) {
                        return new int[]{r, c};
                    }
                }
            }
            return null;
        }

        private void showGameOverDialog() {
            JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
            JLabel message = new JLabel("Поражение! Что дальше?");
            panel.add(message);

            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton restartButton = new JButton("Начать заново");
            restartButton.addActionListener(e -> {
                dispose();
                new HexcellsUI(level);
            });
            buttonPanel.add(restartButton);

            JButton menuButton = new JButton("В главное меню");
            menuButton.addActionListener(e -> {
                dispose();
                new MainMenu();
            });
            buttonPanel.add(menuButton);

            panel.add(buttonPanel);

            JOptionPane.showOptionDialog(
                    HexcellsUI.this,
                    panel,
                    "Игра окончена",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{},
                    null
            );
        }
    }
}
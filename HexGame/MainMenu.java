package HexGame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JFrame {
    private JPanel mainPanel;
    private JPanel levelPanel;
    private BufferedImage backgroundImage;

    public MainMenu() {
        setTitle("Hexcells Infinite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(400, 400));
        setPreferredSize(new Dimension(800, 600));

        // Загружаем фоновое изображение
        try {
            backgroundImage = ImageIO.read(new File("background.png"));
        } catch (IOException e) {
            System.err.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
            backgroundImage = null;
        }

        // Основная панель с главным меню
        mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Заголовок
        JLabel titleLabel = new JLabel("Hexcells Infinite (Game by Leksus)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, gbc);

        // Кнопка "Уровни"
        JButton levelsButton = new JButton("Уровни");
        levelsButton.setPreferredSize(new Dimension(200, 50));
        levelsButton.addActionListener(e -> showLevelMenu());
        mainPanel.add(levelsButton, gbc);

        // Кнопка "Выход"
        JButton exitButton = new JButton("Выход");
        exitButton.setPreferredSize(new Dimension(200, 50));
        exitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(exitButton, gbc);

        // Панель выбора уровня
        levelPanel = new BackgroundPanel();
        levelPanel.setLayout(new GridBagLayout());
        levelPanel.setVisible(false);

        JLabel levelTitle = new JLabel("Выберите уровень");
        levelTitle.setFont(new Font("Arial", Font.BOLD, 24));
        levelTitle.setForeground(Color.WHITE);
        levelPanel.add(levelTitle, gbc);

        JButton easyButton = new JButton("Лёгкий");
        easyButton.setPreferredSize(new Dimension(200, 50));
        easyButton.addActionListener(e -> startGame(Board.Level.EASY));
        levelPanel.add(easyButton, gbc);

        JButton mediumButton = new JButton("Средний");
        mediumButton.setPreferredSize(new Dimension(200, 50));
        mediumButton.addActionListener(e -> startGame(Board.Level.MEDIUM));
        levelPanel.add(mediumButton, gbc);

        JButton hardButton = new JButton("Сложный");
        hardButton.setPreferredSize(new Dimension(200, 50));
        hardButton.addActionListener(e -> startGame(Board.Level.HARD));
        levelPanel.add(hardButton, gbc);

        JButton backButton = new JButton("Назад");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> showMainMenu());
        levelPanel.add(backButton, gbc);

        // Добавляем обе панели в фрейм
        setLayout(new CardLayout());
        getContentPane().add(mainPanel, "Main");
        getContentPane().add(levelPanel, "Levels");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showLevelMenu() {
        mainPanel.setVisible(false);
        levelPanel.setVisible(true);
    }

    private void showMainMenu() {
        levelPanel.setVisible(false);
        mainPanel.setVisible(true);
    }

    private void startGame(Board.Level level) {
        dispose();
        new HexcellsUI(level);
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Растягиваем изображение под размер панели
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
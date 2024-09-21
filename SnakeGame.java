import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    ArrayList<Tile> snakeBody;
    Tile snakeHead, food;
    Random random = new Random();
    int velocityX = 1, velocityY = 0;
    boolean gameOver = false;
    Timer gameLoop;
    JButton restartButton = new JButton("Restart");

    SnakeGame(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();
        setupRestartButton();
    }

    private void initializeGame() {
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        placeFood();
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    private void setupRestartButton() {
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> restartGame());
        this.add(restartButton);
    }

    private void restartGame() {
        gameOver = false;
        initializeGame();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawTile(g, food.x, food.y, Color.red);
        drawTile(g, snakeHead.x, snakeHead.y, Color.green);
        snakeBody.forEach(tile -> drawTile(g, tile.x, tile.y, Color.green));
        displayScore(g);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.gray);
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }
    }

    private void drawTile(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fill3DRect(x * tileSize, y * tileSize, tileSize, tileSize, true);
    }

    private void displayScore(Graphics g) {
        g.setColor(gameOver ? Color.red : Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String text = gameOver ? "Game Over. Score: " : "Score: ";
        g.drawString(text + snakeBody.size(), 5, 20);
    }

    private void placeFood() {
        int x = random.nextInt(boardWidth / tileSize);
        int y = random.nextInt(boardHeight / tileSize);
        food = new Tile(x, y);
    }

    private boolean collision(Tile a, Tile b) {
        return a.x == b.x && a.y == b.y;
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i > 0; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        if (!snakeBody.isEmpty()) {
            snakeBody.get(0).x = snakeHead.x;
            snakeBody.get(0).y = snakeHead.y;
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        checkGameOver();
    }

    private void checkGameOver() {
        if (snakeHead.x < 0 || snakeHead.x * tileSize >= boardWidth ||
                snakeHead.y < 0 || snakeHead.y * tileSize >= boardHeight) {
            gameOver = true;
        }

        for (Tile tile : snakeBody) {
            if (collision(snakeHead, tile)) {
                gameOver = true;
                break;
            }
        }

        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (velocityY == 0) {
                    velocityX = 0;
                    velocityY = -1;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (velocityY == 0) {
                    velocityX = 0;
                    velocityY = 1;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (velocityX == 0) {
                    velocityX = -1;
                    velocityY = 0;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (velocityX == 0) {
                    velocityX = 1;
                    velocityY = 0;
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

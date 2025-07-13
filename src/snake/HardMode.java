import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class HardMode extends JPanel implements ActionListener, KeyListener {

    private class Tile {
        int x, y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private int boardWidth;
    private int boardHeight;
    private int tileSize = 25;

    private Tile snakeHead;
    private LinkedList<Tile> snakeBody;
    private Tile food;
    private Random random;

    private int velocityX = 1;
    private int velocityY = 0;

    private Timer gameLoop;
    private boolean gameOver = false;

    private Stack<String> moveHistory;

    private Tile[] obstacles; 

    public HardMode(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        snakeHead = new Tile(5, 5);
        snakeBody = new LinkedList<>();
        food = new Tile(10, 10);
        random = new Random();

        obstacles = new Tile[] {
            new Tile(7, 7),
            new Tile(12, 12),
            new Tile(15, 5),
            new Tile(18, 18),
            new Tile(4, 3)
        };

        placeFood();

        moveHistory = new Stack<>();

        gameLoop = new Timer(75, this); 
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.setColor(Color.GRAY);
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        g.setColor(Color.orange);
        for (Tile obstacle : obstacles) {
            g.fillRect(obstacle.x * tileSize, obstacle.y * tileSize, tileSize, tileSize);
        }

        g.setColor(Color.red);
        g.fillOval(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        g.setColor(Color.green);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        for (Tile part : snakeBody) {
            g.setColor(new Color(0, 255, 100));
            g.fillRect(part.x * tileSize, part.y * tileSize, tileSize, tileSize);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + snakeBody.size(), 10, 20);

        if (gameOver) {
            g.setColor(Color.black);
            g.fillRect(0, 0, boardWidth, boardHeight);
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Game Over", boardWidth / 2 - 100, boardHeight / 2 - 30);

            g.setColor(Color.PINK);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score: " + snakeBody.size(), boardWidth / 2 - 40, boardHeight / 2);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press R to Restart", boardWidth / 2 - 80, boardHeight / 2 + 30);
            g.drawString("Press P for Last Five Moves", boardWidth / 2 - 110, boardHeight / 2 + 60);
            g.setColor(Color.YELLOW);
            g.drawString("Press E for Easy Mode", boardWidth / 2 - 90, boardHeight / 2 + 90);
        }
    }

    private void placeFood() {
        boolean valid = false;

        while (!valid) {
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);

            valid = true;

            if (snakeHead.x == food.x && snakeHead.y == food.y) {
                valid = false;
                continue;
            }

            for (Tile part : snakeBody) {
                if (part.x == food.x && part.y == food.y) {
                    valid = false;
                    break;
                }
            }

            for (Tile obstacle : obstacles) {
                if (obstacle.x == food.x && obstacle.y == food.y) {
                    valid = false;
                    break;
                }
            }
        }
    }

    private void move() {
        if (moveHistory.size() >= 5) moveHistory.remove(0);
        moveHistory.push("(" + snakeHead.x + "," + snakeHead.y + ")");

        if (snakeHead.x == food.x && snakeHead.y == food.y) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        if (!snakeBody.isEmpty()) {
            snakeBody.addFirst(new Tile(snakeHead.x, snakeHead.y));
            snakeBody.removeLast();
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for (Tile part : snakeBody) {
            if (snakeHead.x == part.x && snakeHead.y == part.y) {
                gameOver = true;
                gameLoop.stop();
            }
        }

        if (snakeHead.x < 0 || snakeHead.y < 0 ||
            snakeHead.x * tileSize >= boardWidth ||
            snakeHead.y * tileSize >= boardHeight) {
            gameOver = true;
            gameLoop.stop();
        }

        for (Tile obstacle : obstacles) {
            if (snakeHead.x == obstacle.x && snakeHead.y == obstacle.y) {
                gameOver = true;
                gameLoop.stop();
            }
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
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (key == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (key == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (key == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (key == KeyEvent.VK_P && gameOver) {
            System.out.println("Last 5 moves:");
            for (String move : moveHistory) {
                System.out.println(move);
            }
        } else if (key == KeyEvent.VK_R && gameOver) {
            snakeHead = new Tile(5, 5);
            snakeBody.clear();
            velocityX = 1;
            velocityY = 0;
            gameOver = false;
            placeFood();
            moveHistory.clear();
            gameLoop.start();
        }
        else if (e.getKeyCode() == KeyEvent.VK_E && gameOver) {
        SnakeGame hardPanel = new SnakeGame(600, 600); 
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.setContentPane(hardPanel);
        currentFrame.revalidate();
        currentFrame.repaint();

        hardPanel.requestFocusInWindow();  
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
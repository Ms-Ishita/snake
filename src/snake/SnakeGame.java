


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }  
    String gameState = "start";
    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;

    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;
        
		gameLoop = new Timer(100, this);
        gameLoop.start();
	}	
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
    if (gameState.equals("start")) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("AlgoSnake", boardWidth / 2 - 100, boardHeight / 2 - 50);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press ENTER to Start", boardWidth / 2 - 100, boardHeight / 2);
    }
    else if (gameState.equals("running")) {
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        for (Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + snakeBody.size(), 10, 20);
        int maxScore = MaxScoreManager.readMaxScore();
        g.drawString("Max Score: " + maxScore, 10, 40);

    }
    else if (gameState.equals("gameOver")) {
        int currentScore = snakeBody.size();
        MaxScoreManager.updateMaxScoreIfNeeded(currentScore);
        int maxScore = MaxScoreManager.readMaxScore();
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Game Over", boardWidth / 2 - 90, boardHeight / 2 - 50);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + snakeBody.size(), boardWidth / 2 - 40, boardHeight / 2 - 10);
        g.drawString("Max Score: " + maxScore, boardWidth / 2 - 60, boardHeight / 2 + 15); 

        if (currentScore >= maxScore) {
        g.setColor(Color.green);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Congratulations! New High Score!", boardWidth / 2 - 150, boardHeight / 2 + 40);
    }
        g.setColor(Color.BLUE);
        g.drawString("Press R to Restart", boardWidth / 2 - 90, boardHeight / 2 + 70);
        g.setColor(Color.YELLOW);
        g.drawString("Press H for Hard Mode", boardWidth / 2 - 110, boardHeight / 2 + 100);
    }
}

    public void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize);
		food.y = random.nextInt(boardHeight/tileSize);
	}

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { 
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || 
            snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight ) { 
            gameOver = true;
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState.equals("running")) {
            move();
            repaint();
            if (gameOver) {
                gameState = "gameOver";
                gameLoop.stop();
            }
        }
    }

    public void restartGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        velocityX = 1;
        velocityY = 0;
        placeFood();
        gameOver = false;
        gameState = "running";
        gameLoop.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    if (gameState.equals("start")) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameState = "running";
            gameLoop.start();
        }
        return;
    }

    if (gameState.equals("gameOver")) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
        else if (e.getKeyCode() == KeyEvent.VK_H) {
        HardMode hardPanel = new HardMode(600, 600); 
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.setContentPane(hardPanel);
        currentFrame.revalidate();
        currentFrame.repaint();

        hardPanel.requestFocusInWindow();  
    }
        return;
    }

    if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
        velocityX = 0;
        velocityY = -1;
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
        velocityX = 0;
        velocityY = 1;
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
        velocityX = -1;
        velocityY = 0;
    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
        velocityX = 1;
        velocityY = 0;
    }
}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
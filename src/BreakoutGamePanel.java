import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BreakoutGamePanel extends JPanel implements KeyListener
{
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 12;
    private static final int BALL_SIZE = 12;
    private static final int BRICK_ROWS = 6;
    private static final int BRICK_COLS = 10;
    private static final int BRICK_HEIGHT = 25;
    private static final int BRICK_GAP = 4;
    private static final int TOP_OFFSET = 80;
    private static final int BASE_BALL_SPEED = 5;

    private Session session;
    private GameFrame frame;
    private int paddleX, paddleY;
    private int paddleWidth = PADDLE_WIDTH;
    private boolean moveLeft, moveRight;
    private ArrayList<Ball> balls;
    private Brick[][] bricks;
    private int brickWidth;
    private ArrayList<Booster> boosters;

    private boolean gameRunning, gameOver, gameWon, paused;
    private int score, highScore, lives;
    private int widePaddleTicks = 0;

    private javax.swing.Timer gameTimer;
    private Random random = new Random();

    private static final Color[] ROW_COLORS = {
            new Color(220, 50, 50),
            new Color(220, 130, 50),
            new Color(220, 200, 50),
            new Color(50, 180, 50),
            new Color(50, 130, 220),
            new Color(150, 50, 220)
    };

    private static final String BOOSTER_WIDE = "WIDE";
    private static final String BOOSTER_MULTIBALL = "MULTIBALL";
    private static final String BOOSTER_LIFE = "LIFE";

    public BreakoutGamePanel(Session session, GameFrame frame)
    {
        this.session = session;
        this.frame = frame;
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        setBackground(new Color(15, 15, 25));

        JButton backButton = new JButton("<- BACK");
        backButton.setBounds(10, 10, 100, 35);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(30, 30, 30));
        backButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e ->
        {
            stopGame();
            frame.showScreen("GAMESELECT");
        });
        add(backButton);

        balls = new ArrayList<>();
        boosters = new ArrayList<>();
        gameTimer = new javax.swing.Timer(16, e -> gameStep());
    }

    public void startGame()
    {
        highScore = session.getBreakoutHighScore();
        score = 0;
        lives = 3;
        gameOver = false;
        gameWon = false;
        paused = false;
        widePaddleTicks = 0;
        paddleWidth = PADDLE_WIDTH;

        brickWidth = (getWidth() - BRICK_GAP * (BRICK_COLS + 1)) / BRICK_COLS;

        paddleX = getWidth() / 2 - PADDLE_WIDTH / 2;
        paddleY = getHeight() - 60;

        balls.clear();
        balls.add(createBall());

        boosters.clear();
        initBricks();

        gameRunning = true;
        gameTimer.start();
        requestFocusInWindow();
    }

    private Ball createBall()
    {
        Ball b = new Ball();
        b.x = getWidth() / 2 - BALL_SIZE / 2;
        b.y = paddleY - BALL_SIZE - 5;
        b.dx = BASE_BALL_SPEED * (random.nextBoolean() ? 1 : -1);
        b.dy = -BASE_BALL_SPEED;
        return b;
    }

    private void initBricks()
    {
        bricks = new Brick[BRICK_ROWS][BRICK_COLS];
        for (int row = 0; row < BRICK_ROWS; row++)
        {
            for (int col = 0; col < BRICK_COLS; col++)
            {
                Brick brick = new Brick();
                brick.x = BRICK_GAP + col * (brickWidth + BRICK_GAP);
                brick.y = TOP_OFFSET + row * (BRICK_HEIGHT + BRICK_GAP);
                brick.color = ROW_COLORS[row];
                brick.alive = true;
                brick.hitsLeft = (row < 2) ? 2 : 1;
                bricks[row][col] = brick;
            }
        }
    }

    public void stopGame()
    {
        gameTimer.stop();
        gameRunning = false;
    }

    private void gameStep()
    {
        if (!gameRunning || paused) return;

        int paddleSpeed = 7;
        if (moveLeft && paddleX > 0)
            paddleX -= paddleSpeed;
        if (moveRight && paddleX + paddleWidth < getWidth())
            paddleX += paddleSpeed;

        if (widePaddleTicks > 0)
        {
            widePaddleTicks--;
            if (widePaddleTicks == 0)
                paddleWidth = PADDLE_WIDTH;
        }

        Iterator<Ball> ballIt = balls.iterator();
        while (ballIt.hasNext())
        {
            Ball ball = ballIt.next();
            ball.x += ball.dx;
            ball.y += ball.dy;

            if (ball.x <= 0) { ball.x = 0; ball.dx = Math.abs(ball.dx); }
            if (ball.x + BALL_SIZE >= getWidth()) { ball.x = getWidth() - BALL_SIZE; ball.dx = -Math.abs(ball.dx); }
            if (ball.y <= TOP_OFFSET - 40) { ball.y = TOP_OFFSET - 40; ball.dy = Math.abs(ball.dy); }

            Rectangle paddleRect = new Rectangle(paddleX, paddleY, paddleWidth, PADDLE_HEIGHT);
            Rectangle ballRect = new Rectangle(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
            if (ballRect.intersects(paddleRect) && ball.dy > 0)
            {
                ball.dy = -Math.abs(ball.dy);
                int hitPos = (ball.x + BALL_SIZE / 2) - (paddleX + paddleWidth / 2);
                ball.dx = hitPos / 6;
                if (ball.dx == 0) ball.dx = 1;
            }

            if (ball.y > getHeight())
            {
                ballIt.remove();
                continue;
            }

            for (int row = 0; row < BRICK_ROWS; row++)
            {
                for (int col = 0; col < BRICK_COLS; col++)
                {
                    Brick brick = bricks[row][col];
                    if (!brick.alive) continue;

                    Rectangle brickRect = new Rectangle(brick.x, brick.y, brickWidth, BRICK_HEIGHT);
                    if (!ballRect.intersects(brickRect)) continue;

                    brick.hitsLeft--;
                    if (brick.hitsLeft <= 0)
                    {
                        brick.alive = false;
                        score += (BRICK_ROWS - row) * 10;
                        maybeSpawnBooster(brick.x + brickWidth / 2, brick.y);
                    }

                    int ballCenterX = ball.x + BALL_SIZE / 2;
                    int ballCenterY = ball.y + BALL_SIZE / 2;
                    int brickCenterX = brick.x + brickWidth / 2;
                    int brickCenterY = brick.y + BRICK_HEIGHT / 2;

                    int overlapX = (BALL_SIZE / 2 + brickWidth / 2) - Math.abs(ballCenterX - brickCenterX);
                    int overlapY = (BALL_SIZE / 2 + BRICK_HEIGHT / 2) - Math.abs(ballCenterY - brickCenterY);

                    if (overlapX < overlapY)
                        ball.dx = -ball.dx;
                    else
                        ball.dy = -ball.dy;

                    break;
                }
            }
        }

        if (balls.isEmpty())
        {
            lives--;
            if (lives <= 0)
            {
                endGame(false);
                return;
            }
            else
            {
                balls.add(createBall());
            }
        }

        Iterator<Booster> boostIt = boosters.iterator();
        while (boostIt.hasNext())
        {
            Booster b = boostIt.next();
            b.y += 3;

            Rectangle boostRect = new Rectangle(b.x - 15, b.y - 15, 30, 30);
            Rectangle paddleRect = new Rectangle(paddleX, paddleY, paddleWidth, PADDLE_HEIGHT);

            if (boostRect.intersects(paddleRect))
            {
                applyBooster(b.type);
                boostIt.remove();
            }
            else if (b.y > getHeight())
            {
                boostIt.remove();
            }
        }

        if (allBricksCleared())
            endGame(true);

        repaint();
    }

    private boolean allBricksCleared()
    {
        for (Brick[] row : bricks)
            for (Brick b : row)
                if (b.alive) return false;
        return true;
    }

    private void maybeSpawnBooster(int x, int y)
    {
        if (random.nextInt(5) != 0) return;

        Booster b = new Booster();
        b.x = x;
        b.y = y;
        int type = random.nextInt(3);
        if (type == 0) { b.type = BOOSTER_WIDE;      b.color = new Color(50, 200, 255);  b.label = "W"; }
        else if (type == 1) { b.type = BOOSTER_MULTIBALL; b.color = new Color(255, 200, 50);  b.label = "M"; }
        else { b.type = BOOSTER_LIFE;      b.color = new Color(100, 255, 100); b.label = "+"; }
        boosters.add(b);
    }

    private void applyBooster(String type)
    {
        switch (type)
        {
            case BOOSTER_WIDE:
                paddleWidth = PADDLE_WIDTH * 2;
                widePaddleTicks = 600;
                break;
            case BOOSTER_MULTIBALL:
                if (!balls.isEmpty())
                {
                    Ball existing = balls.get(0);
                    Ball newBall = new Ball();
                    newBall.x = existing.x;
                    newBall.y = existing.y;
                    newBall.dx = -existing.dx;
                    newBall.dy = existing.dy;
                    balls.add(newBall);
                }
                break;
            case BOOSTER_LIFE:
                lives++;
                break;
        }
    }

    private void endGame(boolean won)
    {
        gameTimer.stop();
        gameRunning = false;
        gameOver = true;
        gameWon = won;

        if (score > highScore)
        {
            highScore = score;
        }
        session.completeGameSession(2,score,0,won ? 4 : 2);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2.setColor(new Color(15, 15, 25));
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawUI(g2);

        if (!gameRunning && !gameOver) return;


        for (int row = 0; row < BRICK_ROWS; row++)
        {
            for (int col = 0; col < BRICK_COLS; col++)
            {
                Brick brick = bricks[row][col];
                if (!brick.alive) continue;
                Color c = brick.hitsLeft == 2 ? brick.color : brick.color.darker();
                g2.setColor(c);
                g2.fillRoundRect(brick.x, brick.y, brickWidth, BRICK_HEIGHT, 6, 6);
                g2.setColor(c.brighter());
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(brick.x, brick.y, brickWidth, BRICK_HEIGHT, 6, 6);
            }
        }

        g2.setColor(new Color(200, 200, 255));
        g2.fillRoundRect(paddleX, paddleY, paddleWidth, PADDLE_HEIGHT, 8, 8);
        if (widePaddleTicks > 0)
        {
            g2.setColor(new Color(50, 200, 255, 150));
            g2.fillRoundRect(paddleX, paddleY, paddleWidth, PADDLE_HEIGHT, 8, 8);
        }

        for (Ball ball : balls)
        {
            g2.setColor(Color.WHITE);
            g2.fillOval(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
            g2.setColor(new Color(200, 200, 255));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
        }

        for (Booster b : boosters)
        {
            g2.setColor(b.color);
            g2.fillOval(b.x - 12, b.y - 12, 24, 24);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Tahoma", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(b.label, b.x - fm.stringWidth(b.label) / 2, b.y + fm.getAscent() / 2 - 2);
        }

        if (widePaddleTicks > 0)
        {
            int barWidth = (int)((widePaddleTicks / 600.0) * 150);
            g2.setColor(new Color(50, 200, 255, 80));
            g2.fillRect(getWidth() - 160, 20, 150, 8);
            g2.setColor(new Color(50, 200, 255));
            g2.fillRect(getWidth() - 160, 20, barWidth, 8);
            g2.setFont(new Font("Tahoma", Font.PLAIN, 10));
            g2.setColor(Color.WHITE);
            g2.drawString("WIDE", getWidth() - 160, 15);
        }

        if (gameOver)
            drawGameOver(g2);
    }

    private void drawUI(Graphics2D g2)
    {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), 55);

        g2.setFont(new Font("Tahoma", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String scoreStr = "Score: " + score;
        g2.drawString(scoreStr, getWidth() / 2 - fm.stringWidth(scoreStr) / 2, 35);

        g2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        g2.setColor(new Color(255, 220, 50));
        g2.drawString("Best: " + highScore, getWidth() / 2 + 80, 35);

        g2.setFont(new Font("Tahoma", Font.BOLD, 16));
        g2.setColor(new Color(255, 80, 80));
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) hearts.append("♥ ");
        g2.drawString(hearts.toString(), 120, 35);

        if (paused)
        {
            g2.setFont(new Font("Tahoma", Font.BOLD, 14));
            g2.setColor(new Color(255, 200, 50));
            g2.drawString("PAUSED", getWidth() / 2 - 30, 20);
        }
    }

    private void drawGameOver(Graphics2D g2)
    {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int bw = 320, bh = 240;
        int bx = getWidth() / 2 - bw / 2;
        int by = getHeight() / 2 - bh / 2;

        g2.setColor(new Color(20, 20, 40, 230));
        g2.fillRoundRect(bx, by, bw, bh, 20, 20);
        g2.setColor(new Color(200, 150, 50));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(bx, by, bw, bh, 20, 20);

        g2.setFont(new Font("Tahoma", Font.BOLD, 30));
        g2.setColor(gameWon ? new Color(100, 255, 100) : Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String title = gameWon ? "YOU WIN!" : "GAME OVER";
        g2.drawString(title, bx + bw / 2 - fm.stringWidth(title) / 2, by + 65);

        g2.setFont(new Font("Tahoma", Font.BOLD, 20));
        g2.setColor(new Color(255, 220, 50));
        String sc = "Score: " + score;
        fm = g2.getFontMetrics();
        g2.drawString(sc, bx + bw / 2 - fm.stringWidth(sc) / 2, by + 105);

        if (score >= highScore && score > 0)
        {
            g2.setFont(new Font("Tahoma", Font.BOLD, 14));
            g2.setColor(new Color(100, 255, 100));
            g2.drawString("New Record!", bx + bw / 2 - 55, by + 135);
        }

        g2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        g2.setColor(Color.WHITE);
        g2.drawString("Press R to play again", bx + bw / 2 - 80, by + 175);
        g2.drawString("Press ESC to go back", bx + bw / 2 - 78, by + 198);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT  || key == KeyEvent.VK_A) moveLeft = true;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) moveRight = true;
        if (key == KeyEvent.VK_P) paused = !paused;
        if (key == KeyEvent.VK_R && gameOver) startGame();
        if (key == KeyEvent.VK_ESCAPE) { stopGame(); frame.showScreen("GAMESELECT"); }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT  || key == KeyEvent.VK_A) moveLeft = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) moveRight = false;
    }

    @Override public void keyTyped(KeyEvent e) {}

    public void refresh(Session session)
    {
        this.session = session;
        highScore = session.getBreakoutHighScore();
    }

    static class Ball
    {
        int x, y, dx, dy;
    }

    static class Brick
    {
        int x, y, hitsLeft;
        Color color;
        boolean alive;
    }

    static class Booster
    {
        int x, y;
        String type, label;
        Color color;
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;


public class SnakeGamePanel extends JPanel implements KeyListener {
    private static final int CELL_SIZE = 25;
    private static final int BASE_SPEED = 150;
    private static final int BOOSTER_LIFETIME = 150;
    private static final int EFFECT_DURATION = 50;

    private Session session;
    public GameFrame frame;

    private int cols, rows, offsetX, offsetY;

    private LinkedList<Point> snake;
    private int dx, dy, nextDx, nextDy;

    private Point food;
    private Image foodImage;
    private Image backgroundImage;

    private ArrayList<Booster> activeBoosters;
    private int boosterSpawnTime;

    private boolean ghostActive, speedActive, slowActive, multiplierActive;
    private int ghostTicks, speedTicks, slowTicks, multiplierTicks;

    private boolean gameRunning, gameOver, paused;
    private int score, highScore;

    private javax.swing.Timer gameTimer;



//    private Color bodyColor = new Color(80, 180, 80);
//    private Color bodyOutlineColor = new Color(40, 120, 40);
//    private Color headColor = new Color(60, 160, 60);

    private static final String[] BOOSTER_TYPES = {"GHOST", "SPEED", "SLOW", "MULTIPLIER", "SHRINK"};
    private static final Color[] BOOSTER_COLOR = {
            new Color(100, 150, 255),
            new Color(255, 165, 0),
            new Color(0, 220, 220),
            new Color(255, 220, 0),
            new Color(255, 100, 180)
    };
    private static final String[] BOOSTER_LABELS = {"G", "S+", "S-", "x2", "<<"};

    public SnakeGamePanel(Session session, GameFrame frame) {
        this.session = session;
        this.frame = frame;
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon("resources/Snake/BackgroundSnake.png").getImage();
        foodImage = new ImageIcon("resources/Snake/food.png").getImage();

        JButton backButton = new JButton("<- BACK");
        backButton.setBounds(10,10, 100, 35);
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

        activeBoosters = new ArrayList<>();
        gameTimer = new Timer(BASE_SPEED, e -> gameStep());
    }

    public void startGame() {
        cols = getWidth() / CELL_SIZE;
        rows = (getHeight() - 60) / CELL_SIZE;
        offsetX = (getWidth() - cols * CELL_SIZE) / 2;
        offsetY = 60;

        snake = new LinkedList<>();
        int startX = cols / 2;
        int startY = rows / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        dx = 1;
        dy = 0;
        nextDx = 1;
        nextDy = 0;

        score = 0;
        gameOver = false;
        gameRunning = true;
        paused = false;

        ghostActive = false;
        speedActive = false;
        slowActive = false;
        multiplierActive = false;

        activeBoosters.clear();
        boosterSpawnTime = 0;
        highScore = session.getSnakeHighScore();

        spawnFood();
        gameTimer.setDelay(BASE_SPEED);
        gameTimer.start();
        requestFocusInWindow();
    }

    public void stopGame() {
        gameTimer.stop();
        gameRunning = false;
    }

    public void gameStep() {
        if (!gameRunning || paused) return;

        dx = nextDx;
        dy = nextDy;

        Point head = snake.getFirst();
        int newX = head.x + dx;
        int newY = head.y + dy;

        if (ghostActive) {
            if (newX < 0) newX = cols - 1;
            if (newX >= cols) newX = 0;
            if (newY < 0) newY = rows - 1;
            if (newY >= rows) newY = 0;
        } else {
            if (newX < 0 || newX >= cols || newY < 0 || newY >= rows) {
                endGame();
                return;
            }
        }

        Point newHead = new Point(newX, newY);

        if (snake.contains(newHead)) {
            endGame();
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score += multiplierActive ? 2 : 1;
            spawnFood();
        } else {
            snake.removeLast();
        }

        Iterator<Booster> it = activeBoosters.iterator();
        while (it.hasNext()) {
            Booster b = it.next();
            if (newHead.x == b.x && newHead.y == b.y) {
                applyBooster(b.type);
                it.remove();
            } else {
                b.ticksAlive++;
                if (b.ticksAlive >= BOOSTER_LIFETIME)
                    it.remove();
            }
        }

        if (ghostActive) {
            ghostTicks--;
            if (ghostTicks <= 0) {
                ghostActive = false;
            }
        }

        if (multiplierActive) {
            multiplierTicks--;
            if (multiplierTicks <= 0) {
                multiplierActive = false;
            }
        }

        if (speedActive) {
            speedTicks--;
            if (speedTicks <= 0) {
                speedActive = false;
                gameTimer.setDelay(getCurrentSpeed());
            }
        }

        if (slowActive) {
            slowTicks--;
            if (slowTicks <= 0) {
                slowActive = false;
                gameTimer.setDelay(getCurrentSpeed());
            }
        }

        boosterSpawnTime++;
        if (boosterSpawnTime >= 100 && activeBoosters.size() < 3) {
            boosterSpawnTime = 0;
            spawnBooster();
        }

        repaint();
    }

    private int getCurrentSpeed() {
        if (speedActive) {
            return BASE_SPEED / 2;
        }
        if (slowActive) {
            return BASE_SPEED * 2;
        }
        return BASE_SPEED;
    }

    private void applyBooster(String type) {
        switch (type) {
            case "GHOST": {
                ghostActive = true;
                ghostTicks = EFFECT_DURATION;
                break;
            }


            case "SPEED": {
                speedActive = true;
                speedTicks = EFFECT_DURATION;
                slowActive = false;
                gameTimer.setDelay(BASE_SPEED / 2);
                break;
            }

            case "SLOW": {
                slowActive = true;
                slowTicks = EFFECT_DURATION;
                speedActive = false;
                gameTimer.setDelay(BASE_SPEED * 2);
                break;
            }

            case "MULTIPLIER": {
                multiplierActive = true;
                multiplierTicks = EFFECT_DURATION * 2;
                break;
            }

            case "SHRINK": {
                for (int i = 0; i < 3 && snake.size() > 3; i++) {
                    snake.removeLast();
                }
                break;
            }
        }
    }

    private void spawnFood() {
        Random rand = new Random();
        Point f;
        do {
            f = new Point(rand.nextInt(cols), rand.nextInt(rows));
        }
        while (snake.contains(f));
        food = f;
    }

    private void spawnBooster() {
        Random random = new Random();
        int typeIndex = random.nextInt(BOOSTER_TYPES.length);
        Point pos;
        do {
            pos = new Point(random.nextInt(cols), random.nextInt(rows));
        }
        while (snake.contains(pos) || pos.equals(food));

        Booster b = new Booster();
        b.x = pos.x;
        b.y = pos.y;
        b.type = BOOSTER_TYPES[typeIndex];
        b.color = BOOSTER_COLOR[typeIndex];
        b.label = BOOSTER_LABELS[typeIndex];
        b.ticksAlive = 0;
        activeBoosters.add(b);
    }

    private void endGame() {
        gameTimer.stop();
        gameRunning = false;
        gameOver = true;

        if (score > highScore) {
            highScore = score;
            session.updateSnakeHighScore(score);
        }
        session.addXP(score > 0 ? 2 : 1);
        session.incrementGameCount(true);
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(30,30,30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawUI(g2);

        if (!gameRunning && !gameOver) return;

        for (int x = 0; x < cols; x++) 
        {
            for (int y = 0; y < rows; y++)
            {
                if ((x + y) % 2 == 0)
                    g2.setColor(new Color(100,180,70));
                else
                    g2.setColor(new Color(80,150,55));
                g2.fillRect(offsetX + x * CELL_SIZE, offsetY + y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        //g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g2.setColor(new Color(0,0,0,80));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(offsetX, offsetY,cols *  CELL_SIZE, rows * CELL_SIZE);

//        for (int x = 0; x <= cols; x++)
//        {
//            g2.drawLine(offsetX + x * CELL_SIZE, offsetY, offsetX + x * CELL_SIZE, offsetY + rows * CELL_SIZE);
//        }
//        for (int y = 0; y <= rows; y++)
//        {
//            g2.drawLine(offsetX, offsetY + y * CELL_SIZE, offsetX + cols * CELL_SIZE, offsetY + y * CELL_SIZE);
//        }

        g2.setColor(new Color(200,150,50));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(offsetX, offsetY, cols * CELL_SIZE, rows * CELL_SIZE);


        if (food != null) {
            g2.drawImage(foodImage,
                    offsetX + food.x * CELL_SIZE,
                    offsetY + food.y * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE, this);
        }

        for (Booster b : activeBoosters) {
            int bx = offsetX + b.x * CELL_SIZE + CELL_SIZE / 2;
            int by = offsetY + b.y * CELL_SIZE + CELL_SIZE / 2;
            int r = CELL_SIZE / 2 - 1;
            g2.setColor(b.color);
            g2.fillOval(bx - r, by - r, r * 2, r * 2);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(bx - r, by - r, r * 2, r * 2);
            g2.setFont(new Font("Tahoma", Font.BOLD, 9));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(b.label, bx - fm.stringWidth(b.label) / 2, by + fm.getAscent() / 2 - 1);
        }

        drawSnake(g2);
        drawEffects(g2);

        if (gameOver)
            drawGameOver(g2);
    }

    private void drawSnake(Graphics2D g2) {
        if (snake == null || snake.isEmpty()) return;

        int alpha = ghostActive ? 150 : 255;


        for (int i = snake.size() - 1; i >= 1; i--) {
            Point curr = snake.get(i);
            Point next = snake.get(i - 1);

            int cx = offsetX + curr.x * CELL_SIZE + CELL_SIZE / 2;
            int cy = offsetY + curr.y * CELL_SIZE + CELL_SIZE / 2;
            int nx = offsetX + next.x * CELL_SIZE + CELL_SIZE / 2;
            int ny = offsetY + next.y * CELL_SIZE + CELL_SIZE / 2;
            int r = CELL_SIZE / 2 - 1;

            float hue = (float) i / snake.size();
            Color segColor = new Color(
                    Color.HSBtoRGB(hue,0.9f,0.95f) & 0x000000FF | (alpha << 24), true
            );

            g2.setColor(segColor);

            int minX = Math.min(cx, nx) - r;
            int minY = Math.min(cy, ny) - r;
            int w = Math.abs(cx - nx) + r * 2;
            int h = Math.abs(cy - ny) + r * 2;
            g2.fillRect(minX, minY, w, h);
            g2.fillRect(cx - r, cy - r, r * 2, r * 2);
        }


        Point h = snake.getFirst();
        int hx = offsetX + h.x * CELL_SIZE + CELL_SIZE / 2;
        int hy = offsetY + h.y * CELL_SIZE + CELL_SIZE / 2;
        int hr = CELL_SIZE / 2 - 1;

        float headHue = (float) (System.currentTimeMillis() % 2000) / 2000f;
        Color headColor = new Color(
                Color.HSBtoRGB(headHue, 1.0f, 1.0f) & 0x000000FF | (alpha << 24), true
        );
        g2.setColor(headColor);
        g2.fillRect(hx - hr,hy -hr, hr * 2, hr * 2);

        int eyeOffset = 4;
        int eyeR = 3;
        int e1x = hx + dx * eyeOffset + dy * eyeOffset;
        int e1y = hy + dy * eyeOffset - dx * eyeOffset;
        int e2x = hx + dx * eyeOffset - dy * eyeOffset;
        int e2y = hy + dy * eyeOffset + dx * eyeOffset;
        g2.setColor(Color.WHITE);
        g2.fillOval(e1x - eyeR, e1y - eyeR, eyeR * 2, eyeR * 2);
        g2.fillOval(e2x - eyeR, e2y - eyeR, eyeR * 2, eyeR * 2);
        g2.setColor(Color.BLACK);
        g2.fillOval(e1x - eyeR / 2, e1y - eyeR / 2, eyeR, eyeR);
        g2.fillOval(e2x - eyeR / 2, e2y - eyeR / 2, eyeR, eyeR);
    }

    public void drawUI(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), 55);

        g2.setFont(new Font("Tahoma", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String scoreString = "Score: " + score;
        g2.drawString(scoreString, getWidth() / 2 - fm.stringWidth(scoreString) / 2, 35);

        g2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        g2.setColor(new Color(255, 200, 50));
        g2.drawString("Best: " + highScore, getWidth() / 2 + 80, 35);

        if (paused) {
            g2.setColor(new Color(255, 200, 50));
            ;
            g2.setFont(new Font("Tahoma", Font.BOLD, 14));
            g2.drawString("Paused", getWidth() / 2 - 80, 35);
        }
    }

    private void drawEffects(Graphics2D g2) {
        int x = getWidth() - 90;
        int y = 75;
        g2.setFont(new Font("Tahoma", Font.BOLD, 11));

        if (ghostActive) {
            g2.setColor(BOOSTER_COLOR[0]);
            g2.drawString("GHOST" + ghostTicks, x, y);
            y += 18;
        }
        if (speedActive) {
            g2.setColor(BOOSTER_COLOR[1]);
            g2.drawString("SPEED" + speedTicks, x, y);
            y += 18;
        }
        if (slowActive) {
            g2.setColor(BOOSTER_COLOR[2]);
            g2.drawString("SLOW" + slowTicks, x, y);
            y += 18;
        }
        if (multiplierActive) {
            g2.setColor(BOOSTER_COLOR[3]);
            g2.drawString("MULTIPLIER" + multiplierTicks, x, y);
        }
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int bw = 320;
        int bh = 230;
        int bx = getWidth() / 2 - bw / 2;
        int by = getHeight() / 2 - bh / 2;

        g2.setColor(new Color(20, 20, 40, 230));
        g2.fillRoundRect(bx, by, bw, bh, 20, 20);
        g2.setColor(new Color(255, 200, 50));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(bx, by, bw, bh, 20, 20);

        g2.setFont(new Font("Tahoma", Font.BOLD, 30));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String go = "GAME OVER";
        g2.drawString(go, bx + bw / 2 - fm.stringWidth(go) / 2, by + 60);

        g2.setFont(new Font("Tahoma", Font.BOLD, 20));
        g2.setColor(new Color(255, 200, 50));
        String sc = "Score: " + score;
        fm = g2.getFontMetrics();
        g2.drawString(sc, bx + bw / 2 - fm.stringWidth(sc) / 2, by + 100);

        if (score >= highScore && score > 0) {
            g2.setColor(new Color(100, 255, 100));
            g2.setFont(new Font("Tahoma", Font.BOLD, 14));
            g2.drawString("New Record!", bx + bw / 2 - 55, by + 128);
        }

        g2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        g2.setColor(Color.WHITE);
        g2.drawString("Press R to play again.", bx + bw / 2 - 75, by + 165);
        g2.drawString("Press ESC to quit.", bx + bw / 2 - 75, by + 188);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            stopGame();
            frame.showScreen("GAMESELECT");
        }

        if (key == KeyEvent.VK_UP && dy != 1) {
            nextDx = 0;
            nextDy = -1;
        }
        if (key == KeyEvent.VK_DOWN && dy != -1) {
            nextDx = 0;
            nextDy = 1;
        }
        if (key == KeyEvent.VK_LEFT && dx != 1) {
            nextDx = -1;
            nextDy = 0;
        }
        if (key == KeyEvent.VK_RIGHT && dx != -1) {
            nextDx = 1;
            nextDy = 0;
        }

        if (key == KeyEvent.VK_W && dy != 1) {
            nextDx = 0;
            nextDy = -1;
        }
        if (key == KeyEvent.VK_S && dy != -1) {
            nextDx = 0;
            nextDy = 1;
        }
        if (key == KeyEvent.VK_A && dx != 1) {
            nextDx = -1;
            nextDy = 0;
        }
        if (key == KeyEvent.VK_D && dx != -1) {
            nextDx = 1;
            nextDy = 0;
        }

        if (key == KeyEvent.VK_P) {
            paused = !paused;
        }

        if (key == KeyEvent.VK_R && gameOver) {
            startGame();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void refresh(Session session) {
        this.session = session;
        highScore = session.getSnakeHighScore();
    }

    static class Booster {
        int x, y, ticksAlive;
        String type, label;
        Color color;
    }
}
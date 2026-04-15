import javax.swing.*;
import java.awt.*;

public class GameSelectPanel extends JPanel
{
    private Image backgroundImage;
    private Session session;
    public GameSelectPanel(Session session, GameFrame frame)
    {
        this.session = session;
        backgroundImage = new ImageIcon("resource/Backgrounds/MenuBackground.png").getImage();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Select a Game");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Tahoma", Font.BOLD, 28));
        title.setForeground(Color.white);

        JButton snakeButton = createGameButton("Snake");
        JButton breakoutButton = createGameButton("Breakout");
        JButton backButton = createGameButton("<-- BACK");
        backButton.setBackground(new Color(100, 60, 30));

        snakeButton.addActionListener(e -> {
            frame.getSnakeGamePanel().refresh(session);
            frame.showScreen("SNAKE");
            frame.getSnakeGamePanel().startGame();
        });


        breakoutButton.addActionListener(e ->
        {
            frame.getBreakoutGamePanel().refresh(session);
            frame.showScreen("BREAKOUT");
            frame.getBreakoutGamePanel().startGame();
        });

        backButton.addActionListener(e -> {
            frame.getMenuPanel().refresh(session);
            frame.showScreen("MENU");
        });

        JPanel card = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10,10,30,200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(200,150,50,180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 25, 25, 25);
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(300, 300));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(20));
        card.add(title);
        card.add(Box.createVerticalStrut(30));
        card.add(snakeButton);
        card.add(Box.createVerticalStrut(15));
        card.add(breakoutButton);
        card.add(Box.createVerticalStrut(15));
        card.add(backButton);
        card.add(Box.createVerticalStrut(20));

        add(Box.createVerticalGlue());
        add(card);
        add(Box.createVerticalGlue());
    }

    private JButton createGameButton(String text)
    {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Tahoma", Font.BOLD, 16));
        button.setForeground(Color.white);
        button.setBackground(new Color(40, 100, 40));
        button.setMaximumSize(new Dimension(240, 45));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        return button;
    }

    public void refresh(Session session)
    {
        this.session = session;
    }

    protected  void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

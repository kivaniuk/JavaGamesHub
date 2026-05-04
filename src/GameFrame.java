import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame
{
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private StatsPanel statsPanel;
    private GamePanel gamePanel;
    private GameSelectPanel gameSelectPanel;
    private SnakeGamePanel snakeGamePanel;
    private BreakoutGamePanel breakoutGamePanel;
    private AdminPanel adminPanel;



    public GameFrame(Session session)
    {
        setTitle("Java Games Hub");
        setSize(450,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(session,this);
        gameSelectPanel = new GameSelectPanel(session, this);
        menuPanel = new MenuPanel(session, this);
        statsPanel = new StatsPanel(session,this);
        gamePanel = new GamePanel(session,this);
        snakeGamePanel = new SnakeGamePanel(session,this);
        breakoutGamePanel = new BreakoutGamePanel(session,this);
        adminPanel = new AdminPanel(session,this);


        mainPanel.add(loginPanel,"LOGIN");
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gameSelectPanel, "GAMESELECT");
        mainPanel.add(statsPanel,"STATS");
        mainPanel.add(gamePanel,"GAME");
        mainPanel.add(snakeGamePanel,"SNAKE");
        mainPanel.add(breakoutGamePanel,"BREAKOUT");
        mainPanel.add(adminPanel,"ADMIN");

        add(mainPanel);
        setVisible(true);
    }
    public void showScreen(String name)
    {
        cardLayout.show(mainPanel, name);
    }

    public MenuPanel getMenuPanel()
    {
        return menuPanel;
    }

    public StatsPanel getStatsPanel()
    {
        return statsPanel;
    }

    public GamePanel getGamePanel()
    {
        return gamePanel;
    }

    public GameSelectPanel getGameSelectPanel()
    {
        return gameSelectPanel;
    }

    public AdminPanel getAdminPanel()
    {
        return adminPanel;
    }



    public SnakeGamePanel getSnakeGamePanel()
    {
        return snakeGamePanel;
    }

    public BreakoutGamePanel getBreakoutGamePanel()
    {
        return breakoutGamePanel;
    }
}

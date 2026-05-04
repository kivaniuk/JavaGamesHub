import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel
{
    private JLabel totalGamesLabel;
    private JLabel snakeGamesLabel;
    private JLabel snakeScoreLabel;
    private JLabel dateCreatedLabel;
    private JLabel breakoutGamesLabel;
    private JLabel breakoutGamesScoreLabel;
    private Image backgroundImage;

    public StatsPanel(Session session, GameFrame frame)
    {
        backgroundImage = new ImageIcon("resources/backgrounds/MenuBackground.png").getImage();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        totalGamesLabel = new JLabel("Total Games: 0");
        snakeGamesLabel = new JLabel("Snake Games: 0");
        snakeScoreLabel = new JLabel("Snake Record: 0");
        dateCreatedLabel = new JLabel("Member since: -");
        breakoutGamesLabel = new JLabel("Breakout games: 0");
        breakoutGamesScoreLabel = new JLabel("Breakout Record: 0");

        dateCreatedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Stats Panel");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font ("Tahoma" , Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 255, 255));

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font ("Tahoma", Font.BOLD, 18));
        backButton.setForeground(new Color(255, 255, 255));
        backButton.setBackground(new Color(0, 0, 0));
        backButton.setBorder(BorderFactory.createRaisedBevelBorder());
        backButton.setFocusPainted(false);
        backButton.setMaximumSize(new Dimension(150, 30));

        backButton.addActionListener(e -> frame.showScreen("MENU"));

        add(Box.createVerticalStrut(20));
        add(titleLabel);
        add(Box.createVerticalStrut(20));
        add(dateCreatedLabel);
        add(Box.createVerticalStrut(10));
        add(totalGamesLabel);
        add(Box.createVerticalStrut(20));
        add(snakeGamesLabel);
        add(Box.createVerticalStrut(10));
        add(snakeScoreLabel);
        add(Box.createVerticalStrut(20));
        add(breakoutGamesLabel);
        add(Box.createVerticalStrut(10));
        add(breakoutGamesScoreLabel);
        add(Box.createVerticalStrut(10));
        add(backButton);
        add(Box.createVerticalGlue());


    }

    private JPanel createStatsPanel(JLabel label)
    {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(236, 233, 216));
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(300, 40));
        label.setFont(new Font ("Tahoma", Font.BOLD, 18));
        label.setForeground(new Color(255, 255, 255));
        statsPanel.add(label);
        return statsPanel;
    }

    public void refresh(Session session)
    {
        String[]stats =session.getUserStats();
        if(stats!=null && stats[0] != null)
        {
            totalGamesLabel.setText("Lifetime XP: " + stats[0]);
            snakeGamesLabel.setText("Total Sessions: " + stats[1]);
            snakeScoreLabel.setText("Snake Record: " + stats[2]);
            breakoutGamesScoreLabel.setText("Breakout Record: " + stats[3]);
            dateCreatedLabel.setText("Member since: " + stats[4]);
            breakoutGamesLabel.setText("level: " + stats[5]);




        }
        else
        {
            totalGamesLabel.setText("Lifetime Xp: 0");
            snakeGamesLabel.setText("Total Sessions: 0");
            snakeScoreLabel.setText("Snake Record: 0");
            breakoutGamesScoreLabel.setText("Breakout Record: 0");
            dateCreatedLabel.setText("Member since: -");
            breakoutGamesLabel.setText("Level: 0");
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0,getWidth(),getHeight(), this);
    }
}

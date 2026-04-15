import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MenuPanel extends JPanel
{
    private Image backgroundImage;
    private JLabel usernameLabel;
    private JLabel levelLabel;
    private JProgressBar xpBar;
    private JLabel xpLabel;
    private JLabel profilePicLabel;
    private Session session;

    public MenuPanel(Session session, GameFrame frame)
    {
        this.session = session;
        backgroundImage = new ImageIcon("resources/Backgrounds/MenuBackground.png").getImage();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // profile picture
        profilePicLabel = new JLabel();
        profilePicLabel.setPreferredSize(new Dimension(80, 80));
        profilePicLabel.setMinimumSize(new Dimension(80, 80));
        profilePicLabel.setMaximumSize(new Dimension(80, 80));
        profilePicLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 50), 3));
        profilePicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton changePhotoButton = new JButton("Change Photo");
        changePhotoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePhotoButton.setForeground(new Color(150, 200, 255));
        changePhotoButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
        changePhotoButton.setBorderPainted(false);
        changePhotoButton.setContentAreaFilled(false);
        changePhotoButton.setFocusPainted(false);
        changePhotoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePhotoButton.addActionListener(e -> showChangePhotoDialog());

        // username
        usernameLabel = new JLabel("Player");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // level
        levelLabel = new JLabel("Level 0");
        levelLabel.setForeground(new Color(255, 200, 50));
        levelLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // xp bar
        xpBar = new JProgressBar(0, 4);
        xpBar.setValue(0);
        xpBar.setMaximumSize(new Dimension(230, 14));
        xpBar.setForeground(new Color(200, 140, 30));
        xpBar.setBackground(new Color(50, 40, 20));
        xpBar.setBorderPainted(false);
        xpBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        xpLabel = new JLabel("0 / 4 XP");
        xpLabel.setForeground(new Color(200, 180, 120));
        xpLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // buttons
        JButton playButton = createMenuButton("Play Games", new Color(180, 120, 20));
        JButton statsButton = createMenuButton("Account Stats", new Color(40, 80, 150));
        JButton settingsButton = createMenuButton("Settings", new Color(40, 80, 150));
        JButton logoutButton = createMenuButton("Logout", new Color(150, 60, 30));

        playButton.addActionListener(e ->
        {
            frame.getGameSelectPanel().refresh(session);
            frame.showScreen("GAMESELECT");
        });

        statsButton.addActionListener(e ->
        {
            frame.getStatsPanel().refresh(session);
            frame.showScreen("STATS");
        });

        logoutButton.addActionListener(e ->
        {
            session.logout();
            frame.showScreen("LOGIN");
        });

        // side by side row for stats + settings
        JPanel middleRow = new JPanel();
        middleRow.setLayout(new BoxLayout(middleRow, BoxLayout.X_AXIS));
        middleRow.setOpaque(false);
        middleRow.setMaximumSize(new Dimension(260, 40));
        middleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsButton.setMaximumSize(new Dimension(125, 35));
        settingsButton.setMaximumSize(new Dimension(125, 35));
        middleRow.add(statsButton);
        middleRow.add(Box.createHorizontalStrut(10));
        middleRow.add(settingsButton);

        // dark card panel
        JPanel card = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 10, 30, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(200, 150, 50, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(280, 420));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(15));
        card.add(profilePicLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(changePhotoButton);
        card.add(Box.createVerticalStrut(8));
        card.add(usernameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(levelLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(xpBar);
        card.add(Box.createVerticalStrut(3));
        card.add(xpLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(playButton);
        card.add(Box.createVerticalStrut(10));
        card.add(middleRow);
        card.add(Box.createVerticalStrut(10));
        card.add(logoutButton);
        card.add(Box.createVerticalStrut(15));

        add(Box.createVerticalGlue());
        add(card);
        add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text, Color bgColor)
    {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(240, 38));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        return button;
    }

    public void refresh(Session session)
    {
        String username = session.getLoggedInUsername();
        int totalXP = session.getUserXP();
        int level = getLevel(totalXP);
        int xpCurrent = getXPForCurrentLevel(totalXP);
        int xpNeeded = getXPNeededForNextLevel(totalXP);

        usernameLabel.setText(username);
        levelLabel.setText("Level " + level);
        xpBar.setMaximum(xpNeeded);
        xpBar.setValue(xpCurrent);
        xpLabel.setText(xpCurrent + " / " + xpNeeded + " XP");

        // load profile picture
        String picPath = "resources/avatars/" + session.getProfilePic();
        ImageIcon icon = new ImageIcon(picPath);
        Image scaled = icon.getImage().getScaledInstance(74, 74, Image.SCALE_SMOOTH);
        profilePicLabel.setIcon(new ImageIcon(scaled));
    }

    public void showChangePhotoDialog()
    {
        JDialog dialog = new JDialog();
        dialog.setTitle("Choose Profile Picture");
        dialog.setSize(340, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("Select a profile picture:");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

        // avatar buttons row
        JPanel avatarRow = new JPanel();
        avatarRow.setLayout(new BoxLayout(avatarRow, BoxLayout.X_AXIS));
        avatarRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarRow.add(Box.createHorizontalGlue());

        // load available avatars from resources/avatars folder
        String[] avatars = {"avatar1.jpg", "avatar2.jpg", "avatar3.png", "avatar4.png"};
        for (String avatarFile : avatars)
        {
            ImageIcon icon = new ImageIcon("resources/avatars/" + avatarFile);
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JButton avatarButton = new JButton(new ImageIcon(scaled));
            avatarButton.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 50), 2));
            avatarButton.setFocusPainted(false);
            avatarButton.setContentAreaFilled(false);
            avatarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            avatarButton.addActionListener(e ->
            {
                session.updateProfilePic(avatarFile);
                refresh(session);
                dialog.dispose();
            });
            avatarRow.add(avatarButton);
            avatarRow.add(Box.createHorizontalStrut(8));
        }
        avatarRow.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(Box.createVerticalStrut(15));
        dialog.add(infoLabel);
        dialog.add(Box.createVerticalStrut(15));
        dialog.add(avatarRow);
        dialog.add(Box.createVerticalStrut(15));
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    public static int getLevel(int totalXP)
    {
        int level = 0;
        int threshold = 4;
        int xpLeft = totalXP;
        while (xpLeft >= threshold)
        {
            xpLeft -= threshold;
            level++;
            threshold = (int)(threshold * 1.5);
        }
        return level;
    }

    public static int getXPForCurrentLevel(int totalXP)
    {
        int threshold = 4;
        int xpLeft = totalXP;
        while (xpLeft >= threshold)
        {
            xpLeft -= threshold;
            threshold = (int)(threshold * 1.5);
        }
        return xpLeft;
    }

    public static int getXPNeededForNextLevel(int totalXP)
    {
        int threshold = 4;
        int xpLeft = totalXP;
        while (xpLeft >= threshold)
        {
            xpLeft -= threshold;
            threshold = (int)(threshold * 1.5);
        }
        return threshold;
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JPanel
{
    private Session session;
    private GameFrame frame;
    private JTable userTable;
    private JTable reportTable;

    public AdminPanel(Session session, GameFrame frame)
    {
        this.session = session;
        this.frame = frame;
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(20, 20, 40));
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setForeground(new Color(255, 200, 50));
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> { session.logout(); frame.showScreen("LOGIN"); });
        topBar.add(titleLabel);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(logoutButton);
        add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("User Management", buildUserPanel());
        tabs.addTab("Weekly Report", buildReportPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildUserPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Username", "Role", "Active", "Created", "Lifetime XP", "Sessions", "Level"};
        userTable = new JTable(new javax.swing.table.DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton deactivateButton = new JButton("Deactivate");
        JButton activateButton = new JButton("Activate");

        refreshButton.addActionListener(e -> loadUsers());
        deactivateButton.addActionListener(e -> setSelectedUserActive(false));
        activateButton.addActionListener(e -> setSelectedUserActive(true));

        buttonRow.add(refreshButton);
        buttonRow.add(deactivateButton);
        buttonRow.add(activateButton);
        panel.add(buttonRow, BorderLayout.SOUTH);

        loadUsers();
        return panel;
    }

    private JPanel buildReportPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel controls = new JPanel(new FlowLayout());
        JTextField weekField = new JTextField("2026-04-27", 12);
        JButton runButton = new JButton("Generate Report");
        controls.add(new JLabel("Week Start (YYYY-MM-DD):"));
        controls.add(weekField);
        controls.add(runButton);
        panel.add(controls, BorderLayout.NORTH);

        String[] columns = {"Username", "Weekly XP", "Sessions", "Position", "Snake Best", "Breakout Best", "Avg Duration"};
        reportTable = new JTable(new javax.swing.table.DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        runButton.addActionListener(e -> loadReport(weekField.getText().trim()));

        return panel;
    }

    private void loadUsers()
    {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
        ResultSet rs = session.getAllUsers();
        try
        {
            while (rs != null && rs.next())
            {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("user_role"),
                        rs.getInt("is_active") == 1 ? "Yes" : "No",
                        rs.getString("created_at"),
                        rs.getInt("lifetime_xp"),
                        rs.getInt("total_sessions"),
                        rs.getInt("level")
                });
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void setSelectedUserActive(boolean active)
    {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        int userId = (int) userTable.getValueAt(row, 0);
        session.setUserActive(userId, active);
        loadUsers();
    }

    private void loadReport(String weekStart)
    {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) reportTable.getModel();
        model.setRowCount(0);
        ResultSet rs = session.generateWeeklyReport(weekStart);
        try
        {
            while (rs != null && rs.next())
            {
                model.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getInt("weekly_xp"),
                        rs.getInt("weekly_sessions"),
                        rs.getInt("leaderboard_position"),
                        rs.getInt("best_snake_score"),
                        rs.getInt("best_breakout_score"),
                        rs.getDouble("avg_session_seconds")
                });
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error loading report: " + e.getMessage());
        }
    }

    public void refresh(Session session)
    {
        this.session = session;
        loadUsers();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(new Color(15, 15, 30));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
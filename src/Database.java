import java.sql.*;

public class Database
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/gamehub_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private Connection connection;

    public Database()
    {
        try
        {
            connection = DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connected to Database successfully!");
        }
        catch (SQLException e)
        {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    public String[] getUser(String username)
    {
        try
        {
            String query = "SELECT username, pin, security_question, security_answer, user_role, profile_picture FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
            {
                String[] user = new String[6];
                user[0] = rs.getString("username");
                user[1] = rs.getString("pin");
                user[2] = rs.getString("security_question");
                user[3] = rs.getString("security_answer");
                user[4] =rs.getString("user_role");
                user[5] = rs.getString("profile_picture");
                return user;
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    public boolean userExists(String username)
    {
        try
        {
            String query = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println("Error checking user: " + e.getMessage());
        }
        return false;
    }
    public void updateProfilePic(String username, String filename)
    {
        try {
            String query = "UPDATE users SET profile_picture = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,filename);
            statement.setString(2,username);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error updating the Profile Picture: " + e.getMessage());
        }
    }

    public boolean createUser(String username, String pin, String securityQuestion, String securityAnswer)
    {
        try
        {
            String query = "INSERT INTO users (username, pin, security_question, security_answer) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, pin);
            statement.setString(3, securityQuestion);
            statement.setString(4, securityAnswer);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next())
            {
                int newUserId = keys.getInt(1);
                String statsQuery = "INSERT INTO user_stats (user_id, week_start_date, week_end_date) VALUES (?, CURDATE(), CURDATE() + INTERVAL 7 DAY)";
                PreparedStatement statsStatement = connection.prepareStatement(statsQuery);
                statsStatement.setInt(1, newUserId);
                statsStatement.executeUpdate();
            }
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("Error Creating user: " + e.getMessage());
        }
        return false;
    }

    public int getUserId(String username)
    {
        try
        {
            String query = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                return rs.getInt("user_id");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error getting user id: " + e.getMessage());
        }
        return -1;
    }

    public int getUserXP(String username)
    {
        try
        {
            String query = "SELECT us.lifetime_xp FROM user_stats us " +
                    "JOIN users u ON us.user_id = u.user_id " +
                    "WHERE u.username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                return rs.getInt("lifetime_xp");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error getting user XP: " + e.getMessage());
        }
        return 0;
    }

    public int getHighScore(String username, int gameID)
    {
        try
        {
            String query = "SELECT hs.score FROM high_scores hs " +
                    "JOIN users u ON hs.user_id = u.user_id " +
                    "WHERE u.username = ? AND hs.game_id = ? ";
            PreparedStatement statement  = connection.prepareStatement(query);
            statement.setString(1,username);
            statement.setInt(2,gameID);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                return rs.getInt("score");
            }
        }
        catch (SQLException e)
            {
            System.out.println("Error getting high score: " + e.getMessage());
            }
        return 0;
    }

    public void completeGameSession(String username, int gameID, int score, int durationSeconds, int xpEarned, int keyPresses, int mouseClicks, int timesPaused, int boostersCollected)
    {
        try
        {
            int userID = getUserId(username);
            if (userID == -1) return;
            String query = "CALL complete_game_session(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            statement.setInt(2, gameID);
            statement.setInt(3, score);
            statement.setInt(4, durationSeconds);
            statement.setInt(5, xpEarned);
            statement.setInt(6, keyPresses);
            statement.setInt(7, mouseClicks);
            statement.setInt(8, timesPaused);
            statement.setInt(9, boostersCollected);
            statement.execute();
        }
        catch (SQLException e)
            {
            System.out.println("Error completing game session: " + e.getMessage());
            }
    }

    public String[] getUserStats(String username)
    {
        String[] stats = new String[6];
        try
        {
            String query = "SELECT u.created_at, us.lifetime_xp, us.total_sessions, " +
                    "us.weekly_xp, us.weekly_sessions, " +
                    "calculate_level(us.lifetime_xp) AS level, " +
                    "COALESCE((SELECT score FROM high_scores hs WHERE hs.user_id = u.user_id AND hs.game_id = 1), 0) AS snake_best, " +
                    "COALESCE((SELECT score FROM high_scores hs WHERE hs.user_id = u.user_id AND hs.game_id = 2), 0) AS breakout_best " +
                    "FROM users u JOIN user_stats us ON u.user_id = us.user_id " +
                    "WHERE u.username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                stats[0] = rs.getString("lifetime_xp");
                stats[1] = rs.getString("total_sessions");
                stats[2] = rs.getString("snake_best");
                stats[3] = rs.getString("breakout_best");
                stats[4] =  rs.getString("created_at");
                stats[5] = rs.getString("level");
            }
        }
        catch (SQLException e)
            {
            System.out.println("Error getting user stats: " + e.getMessage());
            }
        return stats;
    }

    public ResultSet generateWeeklyReport(String weekStart)
    {
        try
        {
            String query = "CALL generate_weekly_report(?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, weekStart);
            return statement.executeQuery();
        }
        catch (SQLException e)
            {
            System.out.println("Error generating weekly report: " + e.getMessage());
            }
        return null;
    }

    public ResultSet getAllUsers()
    {
        try
        {
            String query = "SELECT u.user_id, u.username, u.user_role, u.is_active, " +
                    "u.created_at, us.lifetime_xp, us.total_sessions, " +
                    "calculate_level(us.lifetime_xp) AS level " +
                    "FROM users u JOIN user_stats us ON u.user_id = us.user_id " +
                    "ORDER BY us.lifetime_xp DESC";
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        }
        catch (SQLException e)
            {
            System.out.println("Error getting all users: " + e.getMessage());
            }
        return null;
    }

    public void setUserActive(int userId, boolean active)
    {
        try
        {
            String query = "UPDATE users SET is_active = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, active ? 1 : 0);
            statement.setInt(2,userId);
            statement.executeUpdate();
        }
        catch (SQLException e)
            {
            System.out.println("Error updating user status: " + e.getMessage());
            }
    }

    public void closeConnection()
    {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
                System.out.println("Database connection closed.");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}

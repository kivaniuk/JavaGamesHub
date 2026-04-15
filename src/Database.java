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
            String query = "SELECT username, pin, security_question, security_answer, xp, profile_picture FROM users WHERE username = ?";
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
                user[4] =rs.getString("xp");
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
            String query = "SELECT id FROM users WHERE username = ?";
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

    public void updateXP(String username, int newXP)
    {
        try
        {
            String query = "UPDATE users SET xp = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, newXP);
            statement.setString(2,username);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error Updating the XP: " + e.getMessage());
        }
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
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, pin);
            statement.setString(3, securityQuestion);
            statement.setString(4, securityAnswer);
            statement.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("Error Creating user: " + e.getMessage());
        }
        return false;
    }

    public int getSnakeHighScore(String username)
    {
        try
        {
            String query = "SELECT snake_highscore FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                return rs.getInt("snake_highscore");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error Getting snake highscore: " + e.getMessage());
        }
        return 0;
    }

    public void updateSnakeHighScore(String username,int score)
    {
        try
        {
            String query = "UPDATE users SET snake_highscore = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,score);
            statement.setString(2,username);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error Updating the Snake High Score: " + e.getMessage());
        }
    }

    public String[]getUserScores(String username)
    {
        String[] stats = new String[4];

        try
        {
            String query = "SELECT total_games, snake_games, snake_highscore, date_created FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
            {
                stats[0] =rs.getString("total_games");
                stats[1] = rs.getString("snake_games");
                stats[2] = rs.getString("snake_highscore");
                stats[3] = rs.getString("date_created");
            }
        } catch  (SQLException e)
        {
            System.out.println("Error Getting Stats: " + e.getMessage());
        }
        return stats;
    }

    public int getBreakoutHighScore(String username)
    {
        try
        {
            String query = "SELECT breakout_highscore FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return rs.getInt("breakout_highscore");
        }
        catch (SQLException e)
        {
            System.out.println("Error getting breakout high score: " + e.getMessage());
        }
        return 0;
    }



    public void updateBreakoutHighScore(String username, int score)
    {
        try
        {
            String query = "UPDATE users SET breakout_highscore = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, score);
            statement.setString(2, username);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error updating breakout high score: " + e.getMessage());
        }
    }

    public void incrementGameCount(String username, boolean isSnake)
    {
        try
        {
            String query = "UPDATE users SET total_games = total_games + 1" +
                    (isSnake ? ", snake_games = snake_games + 1 " : " ") +
                    "WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,username);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error Incrementing Game Count: " + e.getMessage());
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

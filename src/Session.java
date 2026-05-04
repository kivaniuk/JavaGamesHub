import java.sql.ResultSet;

public class Session
{
    private Database database;
    private String loggedInUsername;

    public Session()
    {
        database = new Database();
    }
    public boolean login(String username, String pin)
    {
        if (username.isEmpty() || pin.isEmpty())
        {
            return false;
        }
        String[] user = database.getUser(username);

        if (user != null && user[1].equals(pin))
        {
            loggedInUsername = user[0];
            return true;
        }
        return false;
    }

    public boolean register(String username, String pin, String securityQuestion, String securityAnswer)
    {
        if (database.userExists(username))
        {
            return false;
        }

        return database.createUser(username, pin, securityQuestion, securityAnswer);
    }

    public String getProfilePic()
    {
        String[] user = database.getUser(loggedInUsername);
        if (user != null)
        {
            return user[5];
        }
        return "avatar1.jpg";
    }

    public void updateProfilePic(String filename)
    {
        database.updateProfilePic(loggedInUsername, filename);
    }

    public void logout()
    {
        loggedInUsername = null;
    }

    public String getLoggedInUsername()
    {
        return loggedInUsername;
    }

    public int getUserXP()
    {
        return database.getUserXP(loggedInUsername);
    }

    public String getSecurityQuestion(String username)
    {
        String[] user = database.getUser(username);
        if (user != null)
        {
            return user[2];
        }
        return null;
    }

    public boolean verifySecurityAnswer(String username, String answer)
    {
        String[] user = database.getUser(username);
        if (user != null)
        {
            return user[3].equalsIgnoreCase(answer);
        }
        return false;
    }

    public String[] getUserStats()
    {
        return database.getUserStats(loggedInUsername);
    }

    public void completeGameSession(int gameId, int score,int durationSeconds,int xpEarned)
    {
        database.completeGameSession(loggedInUsername, gameId,score,durationSeconds,xpEarned,0,0,0,0);
    }

    public boolean isAdmin()
    {
        String[] user = database.getUser(loggedInUsername);
        if (user != null)
        {
            return user[4].equals("admin");
        }
        return false;
    }

    public int getBreakoutHighScore()
    {
        return database.getHighScore(loggedInUsername,2);
    }

    public String getPinForRecovery(String username)
    {
        String[] user = database.getUser(username);
        if (user != null)
        {
            return user[1];
        }
        return null;
    }

    public void closeDatabase()
    {
        database.closeConnection();
    }

    public int getSnakeHighScore()
    {
        return database.getHighScore(loggedInUsername,1);
    }

    public ResultSet getAllUsers()
    {
        return database.getAllUsers();
    }

    public ResultSet generateWeeklyReport(String weekStart)
    {
        return database.generateWeeklyReport(weekStart);
    }

    public void setUserActive(int userID, boolean active)
    {
        database.setUserActive(userID,active);
    }



}
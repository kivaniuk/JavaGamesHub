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

    public String[] getUserScores()
    {
        return  database.getUserScores(loggedInUsername);
    }

    public void incrementGameCount(boolean isSnake)
    {
        database.incrementGameCount(loggedInUsername, isSnake);
    }

    public boolean register(String username, String pin, String securityQuestion, String securityAnswer)
    {
        if (database.userExists(username))
        {
            return false;
        }

        return database.createUser(username, pin, securityQuestion, securityAnswer);
    }

    public int getUserXP()
    {
        String[] user = database.getUser(loggedInUsername);
        if (user != null)
        {
            return Integer.parseInt(user[4]);
        }
        return 0;
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

    public void addXP(int amount)
    {
        database.updateXP(loggedInUsername, getUserXP() + amount);
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

    public int getBreakoutHighScore()
    {
        return database.getBreakoutHighScore(loggedInUsername);
    }

    public void updateBreakoutHighScore(int score)
    {
        database.updateBreakoutHighScore(loggedInUsername, score);
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
        return database.getSnakeHighScore(loggedInUsername);
    }

    public void  updateSnakeHighScore(int score)
    {
        database.updateSnakeHighScore(loggedInUsername, score);
    }

}
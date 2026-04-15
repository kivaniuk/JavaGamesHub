public class Main
{
    public static void main(String[] args)
    {
        Session session = new Session();
        new GameFrame(session);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> session.closeDatabase()));
    }
}
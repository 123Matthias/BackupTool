package my.backup.backupTool;

public class Properties {
    private static final Properties Singleton = new Properties();
    private final String logFilePath = "data/backup.log";

    private Properties() {

    }

    public static Properties Singleton(){
        return Singleton;
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}

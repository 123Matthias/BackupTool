package my.backup.backupTool;

public class Properties {
    private static final Properties Singleton = new Properties();
    private final String logFilePath = "data/backup.log";
    private final String validationLogFilePath = "data/validation/";
    private int threadCount = 0;

    private Properties() {
        this.threadCount = App.Hardware.preferredThreadCount();
    }

    public static Properties Singleton(){
        return Singleton;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public String getValidationLogFilePath() {
        return validationLogFilePath;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}

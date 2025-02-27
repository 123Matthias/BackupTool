package my.backup.backupTool;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Properties {
    private static final Properties Singleton = new Properties();
    @JsonProperty("logfile-path")
    private String logFilePath;
    @JsonProperty("validation-logfile-path")
    private String validationLogFilePath;
    @JsonProperty("merge-models-path")
    private String mergeModelsStoragePath;
    @JsonProperty("settings-path")
    private String settingsStoragePath;
    public static final String SUB_LOG_FILE_PATH = "/memoria/data/backup.log";
    public static final String SUB_SETTINGS_PATH = "/memoria/data/settings.json";
    public static final String SUB_MERGE_MODEL_STORAGE_PATH = "/memoria/data/mergeModels.json";
    public static final String SUB_VALIDATION_LOG_FILE_PATH = "/memoria/data/validation/";
    private int threadCount = 0;

    private Properties() {
        this.threadCount = App.Hardware.preferredThreadCount();
        if(threadCount <= 0){
            threadCount = 1;
        }
    }

    public static Properties Singleton(){
        return Singleton;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getValidationLogFilePath() {
        return validationLogFilePath;
    }

    public void setValidationLogFilePath(String validationLogFilePath) {
        this.validationLogFilePath = validationLogFilePath;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public String getMergeModelsStoragePath() {
        return mergeModelsStoragePath;
    }

    public void setMergeModelsStoragePath(String mergeModelsStoragePath) {
        this.mergeModelsStoragePath = mergeModelsStoragePath;
    }

    public String getSettingsStoragePath() {
        return settingsStoragePath;
    }

    public void setSettingsStoragePath(String settingsPath) {
        this.settingsStoragePath = settingsPath;
    }
}

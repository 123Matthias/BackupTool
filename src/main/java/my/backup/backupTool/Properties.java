package my.backup.backupTool;

import com.fasterxml.jackson.annotation.JsonProperty;
import my.backup.backupTool.Enumerations.Theme;

import java.nio.file.Paths;


public class Properties {
    private static final Properties Singleton = new Properties();
    @JsonProperty("super-path")
    private String superPath;
    @JsonProperty("logfile-path")
    private String logFilePath;
    @JsonProperty("validation-logfile-path")
    private String validationLogFilePath;
    @JsonProperty("merge-models-path")
    private String mergeModelsStoragePath;
    @JsonProperty("settings-path")
    private String settingsStoragePath;
    //Sub Strings are final not super Strings for choosing a Path
    public static final String SUB_LOG_FILE_PATH = "/memoria/data/backup.log";
    public static final String APP_SETTINGS_PATH = "./memoria/data/settings.json";
    public static final String SUB_MERGE_MODEL_STORAGE_PATH = "/memoria/data/mergeModels.json";
    public static final String SUB_VALIDATION_LOG_FILE_PATH = "/memoria/data/validation/";
    @JsonProperty("thread-count")
    private int threadCount = 0;
    @JsonProperty("save-close-window")
    private boolean saveOnCloseWindow;
    @JsonProperty("theme")
    private Theme theme;

    public Properties() {
        this.threadCount = App.Hardware.preferredThreadCount();
        if(threadCount <= 0){
            threadCount = 1;
        }
        if(this.theme == null){
            this.theme = Theme.DARK;
        }
        if(this.saveOnCloseWindow != true){
            this.saveOnCloseWindow = false;
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

    public String getSuperPath() {
        return superPath;
    }

    public void setSuperPath(String superPath) {
        this.superPath = superPath;
    }

    public boolean validatePath(String pathString) {
        if (pathString == null || pathString.isBlank()) {
            return false;
        }
        return Paths.get(pathString).toFile().exists();
    }

    @JsonProperty("save-close-window")
    public boolean isSaveOnCloseWindow() {
        return saveOnCloseWindow;
    }

    @JsonProperty("save-close-window")
    public void setSaveOnCloseWindow(boolean saveOnCloseWindow) {
        this.saveOnCloseWindow = saveOnCloseWindow;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }



    @Override
    public String toString() {
        return "Properties{" +
                "superPath='" + superPath + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", validationLogFilePath='" + validationLogFilePath + '\'' +
                ", mergeModelsStoragePath='" + mergeModelsStoragePath + '\'' +
                ", settingsStoragePath='" + settingsStoragePath + '\'' +
                ", threadCount=" + threadCount +
                ", saveOnCloseWindow=" + saveOnCloseWindow +
                ", theme=" + theme +
                '}';
    }
}

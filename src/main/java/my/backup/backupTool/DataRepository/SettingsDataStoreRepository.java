package my.backup.backupTool.DataRepository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.backup.backupTool.App;
import my.backup.backupTool.Enumerations.MessageTYPE;
import my.backup.backupTool.Notifications.MessageService;
import my.backup.backupTool.Properties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsDataStoreRepository {

    private static SettingsDataStoreRepository Instance;

    public SettingsDataStoreRepository() {
        getAllAppSettingsFromJSON();
        createMergeModelPathIfNotExists();
        createLogFilePathIfNotExists();
        createValidationLogFilePathIfNotExists();


    }

    public static SettingsDataStoreRepository Singleton() {
        if(Instance == null) {
            Instance = new SettingsDataStoreRepository();
        }
        return Instance;
    }

    /**
     * Creates the Default Storage Path of the Model Data.
     *
     * @return true if a new Default Storage File was created. else false;
     */
    private boolean createMergeModelPathIfNotExists() {
        Path path = Paths.get(App.Properties.getMergeModelsStoragePath());
        if (!Files.exists(path)) {
            App.Properties.setMergeModelsStoragePath(App.Properties.SUB_MERGE_MODEL_STORAGE_PATH);
            File file = new File(App.Properties.getMergeModelsStoragePath());
            return createFile(file);
        }
        return false;
    }

    private boolean createLogFilePathIfNotExists() {
        Path path = Paths.get(App.Properties.getLogFilePath());
        if (!Files.exists(path)) {
            App.Properties.setLogFilePath(App.Properties.SUB_LOG_FILE_PATH);
            File file = new File(App.Properties.getLogFilePath());
            return createFile(file);
        }
        return false;
    }

    private boolean createValidationLogFilePathIfNotExists() {
        Path path = Paths.get(App.Properties.getMergeModelsStoragePath());
        if (!Files.exists(path)) {
            App.Properties.setValidationLogFilePath(App.Properties.SUB_VALIDATION_LOG_FILE_PATH);
            File file = new File(App.Properties.getValidationLogFilePath());
            return createFile(file);
        }
        return false;
    }

    private boolean createFile(File file){
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                return file.createNewFile();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createMergeModelPathIfNotExists(String stringPath) {
        App.Properties.setMergeModelsStoragePath(stringPath + App.Properties.SUB_MERGE_MODEL_STORAGE_PATH);
        File file = new File(App.Properties.getMergeModelsStoragePath());
        return createFile(file);
    }

    public boolean createLogFilePathIfNotExists(String stringPath) {
        App.Properties.setLogFilePath(stringPath + App.Properties.SUB_LOG_FILE_PATH);
        File file = new File(App.Properties.getLogFilePath());
        return createFile(file);
    }

    public boolean createValidationLogFilePathIfNotExists(String stringPath) {
        App.Properties.setValidationLogFilePath(stringPath + App.Properties.SUB_VALIDATION_LOG_FILE_PATH);
        File file = new File(App.Properties.getValidationLogFilePath());
        return createFile(file);
    }

    public boolean saveAppSettings(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        File file = new File(App.Properties.getSettingsStoragePath());
        try {
            objectMapper.writeValue(file, App.Properties);
            return true;
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der geänderten Modell-Liste: " + e.getMessage());
        }
        return false;
    }

    public boolean getAllAppSettingsFromJSON() {
        System.out.println("getAllAppSettingsFromJSON");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File sourceFile = new File(App.Properties.APP_SETTINGS_PATH);

        if (!sourceFile.exists()) {
            System.out.println("Die Datei existiert nicht: " + sourceFile.getAbsolutePath());
            return false;
        }
        try {
            App.Properties.setSuperPath(mapper.readValue(sourceFile, Properties.class).getSuperPath());
            App.Properties.setLogFilePath(mapper.readValue(sourceFile, Properties.class).getLogFilePath());
            App.Properties.setValidationLogFilePath(mapper.readValue(sourceFile, Properties.class).getValidationLogFilePath());
            App.Properties.setMergeModelsStoragePath(mapper.readValue(sourceFile, Properties.class).getMergeModelsStoragePath());
            App.Properties.setSettingsStoragePath(mapper.readValue(sourceFile, Properties.class).getSettingsStoragePath());
            App.Properties.setThreadCount(mapper.readValue(sourceFile, Properties.class).getThreadCount());
            App.Properties.setSaveOnCloseWindow(mapper.readValue(sourceFile, Properties.class).isSaveOnCloseWindow());
            App.Properties.setTheme(mapper.readValue(sourceFile, Properties.class).getTheme());
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void createSaveOnCloseSettingsWindowListener() {
        App.Router.getSettigsStage().setOnCloseRequest(event -> {
            if(App.Properties.isSaveOnCloseWindow()){
                if(App.SettingsDataStore.saveAppSettings()){
                    MessageService.createToast("I saved your Changes", MessageTYPE.SAVE);
                }
                else{
                    MessageService.createToast("i can't save your Changes",MessageTYPE.DANGER);
                }
            }
            event.consume();
            App.Router.getSettigsStage().close();
        });
    }

}

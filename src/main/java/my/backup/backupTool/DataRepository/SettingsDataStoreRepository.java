package my.backup.backupTool.DataRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.backup.backupTool.App;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsDataStoreRepository {

    private static SettingsDataStoreRepository Instance;

    public SettingsDataStoreRepository() {
        createMergeModelPathIfNotExists();
        createLogFilePathIfNotExists();
        createValidationLogFilePathIfNotExists();
        createSettingsFilePathIfNotExists();
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
        App.Properties.setMergeModelsStoragePath(App.Properties.SUB_MERGE_MODEL_STORAGE_PATH);
        File file = new File(App.Properties.getMergeModelsStoragePath());
        return createFile(file);
    }

    private boolean createLogFilePathIfNotExists() {
        App.Properties.setLogFilePath(App.Properties.SUB_LOG_FILE_PATH);
        File file = new File(App.Properties.getLogFilePath());
        return createFile(file);
    }

    private boolean createValidationLogFilePathIfNotExists() {
        App.Properties.setValidationLogFilePath(App.Properties.SUB_VALIDATION_LOG_FILE_PATH);
        File file = new File(App.Properties.getValidationLogFilePath());
        return createFile(file);
    }

    private boolean createSettingsFilePathIfNotExists() {
        App.Properties.setSettingsStoragePath(App.Properties.SUB_SETTINGS_PATH);
        File file = new File(App.Properties.getSettingsStoragePath());
        return createFile(file);
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

        Path path = Paths.get("");
        if(stringPath != null) {
            path = Paths.get(stringPath);
        }

        if(Files.exists(path)) {
            App.Properties.setMergeModelsStoragePath(stringPath + App.Properties.SUB_MERGE_MODEL_STORAGE_PATH);
        }
        File file = new File(App.Properties.getMergeModelsStoragePath());

        return createFile(file);
    }

    public boolean createSettingsFilePathIfNotExists(String stringPath) {

        Path path = Paths.get("");
        if(stringPath != null) {
            path = Paths.get(stringPath);
        }
        if(Files.exists(path)) {
            App.Properties.setSettingsStoragePath(stringPath + App.Properties.SUB_SETTINGS_PATH);
        }
        File file = new File(App.Properties.getSettingsStoragePath());
        return createFile(file);
    }

    public boolean createLogFilePathIfNotExists(String stringPath) {

        Path path = Paths.get("");
        if(stringPath != null) {
            path = Paths.get(stringPath);
        }

        if(path.toFile().exists()) {
            App.Properties.setLogFilePath(stringPath + App.Properties.SUB_LOG_FILE_PATH);
        }
        File file = new File(App.Properties.getMergeModelsStoragePath());
        return createFile(file);
    }

    public boolean createValidationLogFilePathIfNotExists(String stringPath) {

        Path path = Paths.get("");
        if(stringPath != null) {
            path = Paths.get(stringPath);
        }
        if(path.toFile().exists()) {
            App.Properties.setValidationLogFilePath(stringPath + App.Properties.SUB_VALIDATION_LOG_FILE_PATH);
        }
        File file = new File(App.Properties.getMergeModelsStoragePath());
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


    public void createSaveOnCloseSettingsWindowListener() {
        App.Router.getSettigsStage().setOnCloseRequest(event -> {
            // Hier speicherst du alle notwendigen Daten
            App.SettingsDataStore.saveAppSettings();
            event.consume();
            App.Router.getSettigsStage().close();
        });
    }

}

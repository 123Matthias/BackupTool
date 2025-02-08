package my.backup.backupTool.DataRepository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.backup.backupTool.Model.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaseDataStoreRepository implements IDataStore {

    private static volatile BaseDataStoreRepository Instance = null;
    private String storagePath;
    private final String DEFAULT_STORAGE_PATH = "./data/mergeDataSettings.json"; // Standardpfad als relativer Pfad
    private List<BaseModel> modelList;
    private BaseModel lastSelectedModel;

    private BaseDataStoreRepository() {
        modelList = getJSONasList();
    }

    public static BaseDataStoreRepository Singleton() {
        if(Instance == null) {
            synchronized (BaseDataStoreRepository.class) {
                Instance = new BaseDataStoreRepository();
            }
        }
        return Instance;
    }




    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getStoragePath() {

        return storagePath == null ? DEFAULT_STORAGE_PATH : storagePath;
    }


    /**
     * Saves the given model as a JSON entry in a predefined storage path.
     * If no path is defined, the default path will be used.
     * Creates a new UUID for the model.
     * If the model already has a UUID, it will be retained and not changed.
     *
     * @param model The BaseModel instance to be saved as an entry in the JSON file.
     * @return true if the model was saved successfully, false in case of an IO-Exception.
     */
    public boolean saveModelAsJSON(BaseModel model) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        File file = new File(getStoragePath());

        try {
            List<BaseModel> modelList = new ArrayList<>();

            if (file.exists() && file.length() > 0) {
                modelList = objectMapper.readValue(file, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, BaseModel.class));
            }

            //Update oder Create wenn uid schon existiert
            if (model.getUid() != null && !model.getUid().isEmpty()) {
                for (int i = 0; i < modelList.size(); i++) {
                    if (modelList.get(i).getUid() == null) {
                        continue;
                    }
                    if (modelList.get(i).getUid().equals(model.getUid())) {
                        modelList.set(i, model);
                        //The global List has to be updated. Saved will be the local var. reloading Json after program start produces reference problems
                        this.modelList.set(i,model);
                        break;
                    }
                }
            } else {
                model.setUid(UUID.randomUUID().toString());
                modelList.add(model);
                //The global List has to be updated. Saved will be the local var. reloading Json after Program start produces reference problems
                this.modelList.add(model);
            }

            objectMapper.writeValue(file, modelList);
            //   System.out.println("Daten wurden erfolgreich gespeichert: " + getStoragePath());
            return true;

        } catch (IOException e) {
            System.err.println("Fehler beim Speichern des Modells als JSON: " + e.getMessage());
            return false;
        }
    }


    private boolean saveListAsJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        File file = new File(getStoragePath());

        try {
            objectMapper.writeValue(file, this.modelList);
            return true;
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der geänderten Modell-Liste: " + e.getMessage());
        }
        return false;
    }
    /**
     * Gets one model from the JSON File with the UUID.
     *
     * @param id of Type String. The auto Created UUID.
     * @return the model with the given id in the Param.
     */
    public BaseModel getModelById(String id) {
        for (BaseModel entry : this.modelList) {
            if (entry.getUid() != null && entry.getUid().equals(id)) {
                System.out.println("GetModel.ByID:" + entry.getUid());
                lastSelectedModel = entry;
                return entry;
            }
        }
        return null;
    }

    /**
     * Retrieves the current list of models.
     * @return A list of models of type BaseModel.
     */
    public List<BaseModel> getModelList() {
        return this.modelList;
    }

    public boolean deleteModelById_KeepBackup(String uid) {
        for (int i = 0; i < this.modelList.size(); i++) {
            BaseModel entry = this.modelList.get(i);
            if (entry.getUid() != null && entry.getUid().equals(uid)) {
                this.modelList.remove(i);
                return saveListAsJSON();
            }
        }
        return false;
    }

    public boolean deleteModelAndBackupById(String uid) {
        for (int i = 0; i < this.modelList.size(); i++) {
            BaseModel entry = this.modelList.get(i);
            if (entry.getUid() != null && entry.getUid().equals(uid)) {
                this.deleteFolderAndContents(new File(entry.getTarget()));
                this.modelList.remove(i);
                return saveListAsJSON();
            }
        }
        return false;
    }


    /**
     * Creates the Default Storage Path of the Model Data.
     *
     * @return true if a new Default Storage File was created. else false;
     */
    private boolean createDefaultStorageFile() {
        try {
            File file = new File(getStoragePath());
            File parentDir = file.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
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

    /**
     * Reads and returns a list of models from the JSON file.
     *
     * @return A List of BaseModel instances.
     */
    private List<BaseModel> getJSONasList() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // JSON-Datei einlesen
        File sourceFile = new File(getStoragePath());
        System.out.println("------Storage Path: " + sourceFile.getAbsolutePath());
        createDefaultStorageFile();
        if (!sourceFile.exists()) {
            System.out.println("Die Datei existiert nicht: " + sourceFile.getAbsolutePath());
        }

        if (sourceFile.length() == 0) {
            return new ArrayList<>();
        }

        // Daten in eine Liste von BaseModel-Objekten einlesen
        List<BaseModel> dataList = null;

        try {
            dataList = mapper.readValue(
                    sourceFile,
                    mapper.getTypeFactory().constructCollectionType(List.class, BaseModel.class)
            );
        } catch (IOException e) {
            dataList = new ArrayList<>();
            System.out.println(e.getMessage());
        }

        return dataList;
    }


    /**
     * Deleting all Files and then all Subfolders and then the selected Folder (param)
     * @param directory The Directory to delete recurse
     */
    private void deleteFolderAndContents(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles(); //Array of all Files in a Directory
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolderAndContents(file); //Recursion for Folders
                    }
                    file.delete();
                }
            }
          //  directory.delete();
        }

    }

    public BaseModel getLastSelectedModel() {
        return lastSelectedModel;
    }
}


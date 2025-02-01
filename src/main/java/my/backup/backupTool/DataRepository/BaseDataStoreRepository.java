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

    private String storagePath;
    private final String DEFAULT_STORAGE_PATH = "./data/mergeDataSettings.json"; // Standardpfad als relativer Pfad
    private List<BaseModel> modelList;

    public BaseDataStoreRepository() {
        modelList = getJSONasList();
    }

    public BaseDataStoreRepository(String storagePath) {
        this();
        this.storagePath = storagePath;
    }



    public List<BaseModel> getModelList() {
        return this.modelList;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getStoragePath() {

        return storagePath == null ? DEFAULT_STORAGE_PATH : storagePath;
    }

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
                        break;
                    }
                }
            } else {
                model.setUid(UUID.randomUUID().toString());
                modelList.add(model);
            }

            objectMapper.writeValue(file, modelList);

            //   System.out.println("Daten wurden erfolgreich gespeichert: " + getStoragePath());
            this.modelList = getJSONasList();
            return true;

        } catch (IOException e) {
            System.err.println("Fehler beim Speichern des Modells als JSON: " + e.getMessage());
            return false;
        }
    }

    public boolean createDefaultStorageFile() {
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

    public BaseModel getModelById(String id) {
        for (BaseModel entry : this.modelList) {
            if (entry.getUid() != null && entry.getUid().equals(id)) {
                System.out.println("GetModel.ByID:" + entry.getUid());
                return entry;
            }
        }
        return null;
    }
}


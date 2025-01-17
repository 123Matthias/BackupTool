package my.backup.backupTool.Data;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.IModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseData implements IStoreData, ILoadData {


    private String storagePath;
    private final String DEFAULT_STORAGE_PATH = "./data/mergeDataSettings.json"; // Standardpfad als relativer Pfad

    private ArrayList<IModel> modelList;

    // Standardkonstruktor
    public BaseData(){
        modelList = new ArrayList<>();
    }

    // Konstruktor mit benutzerdefiniertem Pfad
    public BaseData(String storagePath) {
        this();
        this.storagePath = storagePath;
    }

    // Getter und Setter für storagePath und modelList
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public ArrayList<IModel> getModelList() {
        return modelList;
    }

    public void setModelList(ArrayList<IModel> modelList) {
        this.modelList = modelList;
    }

    // Methode, die den Pfad für das Speichern der Daten zurückgibt
    public String getStoragePath() {

        return storagePath == null ? DEFAULT_STORAGE_PATH : storagePath;
    }


    public boolean saveModelAsJSON(IModel model) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        File file = new File(getStoragePath());

        try {
            List<IModel> modelList = new ArrayList<>();

            if (file.exists() && file.length() > 0) {
                modelList = objectMapper.readValue(file,    objectMapper
                                                            .getTypeFactory()
                                                            .constructCollectionType(List.class, BaseModel.class));
            }

            modelList.add(model);

            objectMapper.writeValue(file, modelList);

            System.out.println("Daten wurden erfolgreich gespeichert: " + getStoragePath());
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

    @Override
    public List<IModel> getAllAsList() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // JSON-Datei einlesen
        File sourceFile = new File(getStoragePath());
        createDefaultStorageFile();
        if (!sourceFile.exists()) {
            throw new IOException("Die Datei existiert nicht: " + sourceFile.getAbsolutePath());
        }

        if(sourceFile.length() == 0){
            return new ArrayList<>();
        }

        // Daten in eine Liste von BaseModel-Objekten einlesen
        List<IModel> dataList = mapper.readValue(
                sourceFile,
                mapper.getTypeFactory().constructCollectionType(List.class, BaseModel.class)
        );

        // Daten verarbeiten
        dataList.forEach(System.out::println);

        return dataList;
    }

}

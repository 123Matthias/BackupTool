package my.backup.backupTool.Data;

import my.backup.backupTool.Model.IModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MergeData implements IStoreData {

    private String storagePath;
    private final String DEFAULT_STORAGE_PATH = "mergeDataSettings.json"; // Standardpfad als relativer Pfad

    private ArrayList<IModel> modelList;

    // Standardkonstruktor
    public MergeData(){
        modelList = new ArrayList<>();
    }

    // Konstruktor mit benutzerdefiniertem Pfad
    public MergeData(String storagePath) {
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

    // Methode zum Speichern eines Modells als JSON
    public boolean saveModelAsJSON(IModel model) {
        ObjectMapper objectMapper = new ObjectMapper();
        modelList.add(model);
        System.out.println("Stoooooraggeeee Path: " + getStoragePath());

        try {
            File file = new File(getStoragePath());
            if (!file.exists()) {
                file.createNewFile();  // Erstelle die Datei, falls sie nicht existiert
            }
            objectMapper.writeValue(file, modelList);
            System.out.println("Daten wurden erfolgreich gespeichert: " + getStoragePath());
            return true;
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern des Modells als JSON: " + e.getMessage());
            return false;
        }
    }
}

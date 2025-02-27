package my.backup.backupTool.DataRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.backup.backupTool.Model.BaseModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IDataStore extends ILoadData {

    /**
     * Saves the given model as a JSON entry in a predefined storage path.
     * If no path is defined, the default path will be used.
     * Creates a new UUID for the model.
     * If the model already has a UUID, it will be retained and not changed.
     *
     * @param model The BaseModel instance to be saved as an entry in the JSON file.
     * @return true if the model was saved successfully, false in case of an IO-Exception.
     */
    boolean saveModelAsJSON(BaseModel model);
    boolean deleteModelAndBackupById(String uid);
    boolean deleteModelById_KeepBackup(String uid);
    BaseModel getLastSelectedModel();

    /**
     * Save a List of Models as JSON.
     * The List is the Model List of all Models in the Data Repository;
     * @return true or false on success
     */
    boolean saveModelListAsJSON();
    boolean updateModelInList(BaseModel model);

    void createSaveOnCloseMainWindowListener();
}

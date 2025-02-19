package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.BaseModel;

import java.util.List;

public interface ILoadData {
    /**
     * Gets one model by UUID from the loaded List in the Data Store Repository.
     *
     * @param id of Type String. The auto Created UUID.
     * @return the model with the given id in the Param.
     */
    BaseModel getModelById(String id);

    /**
     * Retrieves the current list of models.
     * @return A list of models of type BaseModel.
     */
    List<BaseModel> getModelList();

}

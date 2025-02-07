package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.BaseModel;

import java.util.List;

public interface IDataStore extends ILoadData {

    boolean saveModelAsJSON(BaseModel modelList);
    List<BaseModel> getModelList();
    boolean deleteModelAndBackupById(String uid);
    boolean deleteModelById_KeepBackup(String uid);
}

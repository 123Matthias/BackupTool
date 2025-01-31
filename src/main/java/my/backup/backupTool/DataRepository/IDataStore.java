package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.BaseModel;

public interface IDataStore extends ILoadData {

    boolean saveModelAsJSON(BaseModel modelList);
}

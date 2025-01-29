package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.IModel;

public interface IDataStore extends ILoadData {

    boolean saveModelAsJSON(IModel modelList);
}

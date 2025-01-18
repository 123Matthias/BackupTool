package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.IModel;

public interface IStoreData {

    boolean saveModelAsJSON(IModel modelList);
}

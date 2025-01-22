package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.IModel;

public interface IStoreData extends ILoadData {

    boolean saveModelAsJSON(IModel modelList);
}

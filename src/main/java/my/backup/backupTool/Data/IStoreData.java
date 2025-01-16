package my.backup.backupTool.Data;

import my.backup.backupTool.Model.IModel;

public interface IStoreData {

    boolean saveModelAsJSON(IModel modelList);
}

package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.BaseModel;

import java.util.List;

public interface ILoadData {
    List<BaseModel> getAllAsList();
    BaseModel getModelById(String id);
}

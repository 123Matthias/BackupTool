package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.BaseModel;

import java.util.List;

public interface ILoadData {
    BaseModel getModelById(String id);
}

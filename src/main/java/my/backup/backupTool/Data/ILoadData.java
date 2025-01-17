package my.backup.backupTool.Data;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.IModel;

import java.io.IOException;
import java.util.List;

public interface ILoadData {
    List<IModel> getAllAsList() throws IOException;
}

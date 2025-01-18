package my.backup.backupTool.DataRepository;

import my.backup.backupTool.Model.IModel;

import java.io.IOException;
import java.util.List;

public interface ILoadData {
    List<IModel> getAllAsList() throws IOException;
    IModel getModelById(String id) throws IOException;
}

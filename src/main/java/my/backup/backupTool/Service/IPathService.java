package my.backup.backupTool.Service;

import my.backup.backupTool.Model.IModel;


public interface IPathService {
    IModel setModelPath(String source, String target, IModel model);
}
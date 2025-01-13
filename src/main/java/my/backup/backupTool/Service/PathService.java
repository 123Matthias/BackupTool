package my.backup.backupTool.Service;


import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.IModel;

public class PathService implements IPathService {

    public IModel setModelPath(String source, String target, IModel model){
        if (model == null) {
            throw new IllegalArgumentException("BaseModel darf nicht null sein.");
        }
        if(source == null || target == null){
            throw  new IllegalArgumentException("Source and Target must have a value");
        }
        model.setSource(source);
        model.setTarget(target);
        return model;
    }
}



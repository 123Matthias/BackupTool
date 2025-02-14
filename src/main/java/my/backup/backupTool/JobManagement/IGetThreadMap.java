package my.backup.backupTool.JobManagement;

import my.backup.backupTool.Model.BaseModel;

import java.util.HashMap;

public interface IGetThreadMap {
    HashMap<Thread, BaseModel> getThreadMap();
}

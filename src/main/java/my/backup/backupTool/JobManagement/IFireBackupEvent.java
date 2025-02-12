package my.backup.backupTool.JobManagement;

import my.backup.backupTool.Model.BaseModel;

public interface IFireBackupEvent {
    boolean fireBackupEvent(BaseModel model);
}
